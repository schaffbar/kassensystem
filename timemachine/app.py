from flask import Flask, flash, redirect, render_template, request, url_for
from flask_admin import Admin
from flask_admin.contrib.sqla import ModelView
from flask_sock import Sock
from sqlalchemy.orm import configure_mappers
from models import Clipboard, Device, Card, CardAction
from database import db
from messages import (
    CounterCardRequest,
    CounterCardResponse,
    DeviceInitRequest,
    DeviceInitResponse,
    DeviceCardRequest,
    DeviceCardResponse,
)
from typing import Optional
import json
import datetime
import socket
import threading
from templates.forms import CheckoutForm, LookupForm, RegisterForm
from util import sum_times, validate_data
from flask_bootstrap import Bootstrap5


app = Flask(__name__)
app.secret_key = "51a9e9a11f8f7134c177bacb"

app.config["BOOTSTRAP_SERVE_LOCAL"] = True
bootstrap = Bootstrap5(app)

sock = Sock(app)

app.config["SQLALCHEMY_DATABASE_URI"] = "sqlite:///project.db"
db.init_app(app)

with app.app_context():
    db.create_all()

admin = Admin(app)

configure_mappers()


class CardActionView(ModelView):
    # column_display_pk = True
    column_hide_backrefs = False
    column_searchable_list = ["card_id"]
    column_default_sort = ("created", True)


admin.add_view(ModelView(Device, db.session))
admin.add_view(ModelView(Card, db.session))
admin.add_view(CardActionView(CardAction, db.session))
admin.add_view(ModelView(Clipboard, db.session))


@app.route("/")
def index():
    return render_template("index.html")


@app.route("/register", methods=("GET", "POST"))
def register():
    form = RegisterForm(request.form)

    if request.method == "POST" and form.validate():
        Card.set_name(form.rfid.data, form.name.data)
        flash("Person eingetragen")
        return redirect(url_for("register"))

    rfid = Clipboard.get_rfid("Counter")
    form.rfid.data = rfid
    return render_template("register.html", form=form)


@app.route("/lookup", methods=("GET", "POST"))
def lookup():
    form = LookupForm()

    if request.method == "POST" and form.validate():
        return redirect(url_for("checkout", rfid=form.rfid.data))

    rfid = Clipboard.get_rfid("Counter")
    form.rfid.data = rfid
    return render_template("lookup.html", form=form)


@app.route("/checkout/<rfid>", methods=("GET", "POST"))
def checkout(rfid: str):
    form = CheckoutForm()

    if request.method == "POST" and form.validate():
        CardAction.mark_as_processed(rfid)
        flash("Als bezahlt markiert")
        return redirect(url_for("lookup"))

    card = Card.get_by_rfid(rfid)
    last_actions = CardAction.get_last_actions(card)
    min, _ = sum_times(last_actions)

    return render_template(
        "checkout.html",
        form=form,
        card=card,
        last_actions=last_actions,
        elapsed_min=min,
    )


def handle_client(client_socket):
    with app.app_context():
        with client_socket as sock:
            while True:
                message = sock.recv(1024).decode("utf-8")
                if len(message) == 0:
                    continue

                print(message, flush=True)

                data = json.loads(message)

                # ORDER MATTERS! Less specific messages to the back
                accepted_models = [
                    CounterCardRequest,
                    DeviceCardRequest,
                    DeviceInitRequest,
                ]

                validated_object = validate_data(data, accepted_models)

                if not validated_object:
                    continue

                print(validated_object.__class__.__name__)

                if DeviceInitRequest.__name__ == validated_object.__class__.__name__:
                    request: DeviceInitRequest = validated_object
                    handle_device_init(sock, request)

                elif DeviceCardRequest.__name__ == validated_object.__class__.__name__:
                    request: DeviceCardRequest = validated_object
                    handle_device_card(sock, request)

                elif CounterCardRequest.__name__ == validated_object.__class__.__name__:
                    request: CounterCardRequest = validated_object
                    handle_counter_card(sock, request)

                else:
                    print("{}")
                    sock.sendall("{}")


def handle_device_init(sock: Sock, request: DeviceInitRequest):
    device = (
        db.session.execute(db.select(Device).filter_by(mac=request.MACADDR))
    ).scalar_one_or_none()

    if device is None or device.usecase == "":
        if device is None:
            new_device = Device(mac=request.MACADDR)
            db.session.add(new_device)
            db.session.commit()

        response = DeviceInitResponse(
            STATE="ERROR",
            DEVNAME="",
            DEVUSECASE="ERROR",
            STARTHTTP="",
            DEVIP="",
            SWITCHON="",
            SWITCHOFF="",
            TERMINAL="",
            ERROR="Gerät unbekannt!",
        )
    else:
        response = DeviceInitResponse(
            STATE="START",
            DEVNAME="",
            STARTHTTP="",
            DEVIP="",
            SWITCHON="",
            SWITCHOFF="",
            DEVUSECASE=device.usecase,
            TERMINAL=device.terminal,
            ERROR="",
        )

    print(response.model_dump_json())
    sock.sendall(response.model_dump_json().encode("utf-8"))


def handle_counter_card(sock: Sock, request: CounterCardRequest):

    device: Optional[Device] = (
        db.session.execute(db.select(Device).filter_by(mac=request.MACADDR))
    ).scalar_one_or_none()

    card: Optional[Card] = (
        db.session.execute(db.select(Card).filter_by(rfid=request.RFID))
    ).scalar_one_or_none()

    if device is not None:
        if device.usecase == "C":

            if card is None:
                card = Card(
                    rfid=request.RFID,
                    name="Neue Karte",
                )
                db.session.add(card)
                db.session.commit()

            Clipboard.set_rfid(device.terminal, card.rfid)

            response = CounterCardResponse(
                DEVUSECASE="C",
                ERROR="",
                STATE="END",
                ICON="OK",
            )
            print(response.model_dump_json())
            sock.sendall(response.model_dump_json().encode("utf-8"))
            return


def handle_device_card(sock: Sock, request: DeviceCardRequest):

    device: Optional[Device] = (
        db.session.execute(db.select(Device).filter_by(mac=request.MACADDR))
    ).scalar_one_or_none()

    card: Optional[Card] = (
        db.session.execute(db.select(Card).filter_by(rfid=request.RFID))
    ).scalar_one_or_none()

    if card is None:
        response = DeviceCardResponse(
            CUSTOMERNAME="XXX",
            CUSTOMERSTARTSTOP="",
            ICON="NOREG",
            STATE="END",
            UNITS="00:00",
            ERROR="RFID unbekannt!",
            DEVUSECASE=device.usecase if device is not None else "",
        )

        print(response.model_dump_json())
        sock.sendall(response.model_dump_json().encode("utf-8"))
        return

    elif device is not None:
        last_actions = CardAction.get_last_actions(card)

        if device.usecase == "G":
            response = DeviceCardResponse(
                CUSTOMERNAME=card.name,
                CUSTOMERSTARTSTOP="",
                ICON="",
                STATE="END",
                UNITS="",
                ERROR="",
                DEVUSECASE="G",
            )

            now = datetime.datetime.now()
            if len(last_actions) == 0 or last_actions[-1].type == "checkout":
                new_action = CardAction(
                    device=device,
                    card=card,
                    type="checkin",
                    processed=False,
                )
                db.session.add(new_action)
                db.session.commit()

                # hotfix the current list
                new_actions = list(last_actions)
                new_actions.append(new_action)
                min, sec = sum_times(new_actions)

                response.ICON = "HI"
                response.CUSTOMERSTARTSTOP = now.strftime("%H:%M")
                response.UNITS = f"{min}:{sec}"

            else:
                last_action = last_actions[-1]

                new_action = CardAction(
                    device=device,
                    card=card,
                    type="checkout",
                    processed=False,
                )
                db.session.add(new_action)
                db.session.commit()

                # hotfix the current list
                all_actions = list(last_actions)
                all_actions.append(new_action)
                min, sec = sum_times(all_actions)

                response.ICON = "BYE"
                response.CUSTOMERSTARTSTOP = (
                    last_action.created.strftime("%H:%M")
                    + " - "
                    + now.strftime("%H:%M")
                )
                response.UNITS = f"{min}:{sec}"

            print(response.model_dump_json())
            sock.sendall(response.model_dump_json().encode("utf-8"))


def main():
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    server.bind(("0.0.0.0", 5000))
    server.listen(5)
    print("Server listening on port 5000")

    while True:
        client_socket, addr = server.accept()
        print(f"Accepted connection from {addr}")
        client_handler = threading.Thread(target=handle_client, args=(client_socket,))
        client_handler.start()


if __name__ == "__main__":
    main()
