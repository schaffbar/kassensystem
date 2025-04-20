from flask import Flask
from flask_admin import Admin
from flask_admin.contrib.sqla import ModelView
from flask_sock import Sock
from sqlalchemy.orm import configure_mappers
from models import Device, Card, CardAction
from database import db
from messages import (
    DeviceInitRequest,
    DeviceInitResponse,
    DeviceCardRequest,
    DeviceCardResponse,
)
from typing import Optional, Sequence
import json
import datetime
import socket
import threading
from util import sum_times, validate_data


app = Flask(__name__)
app.secret_key = "51a9e9a11f8f7134c177bacb"

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
                accepted_models = [DeviceCardRequest, DeviceInitRequest]

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
        last_actions: Sequence[CardAction] = (
            (
                db.session.execute(
                    db.select(CardAction)
                    .filter_by(
                        device_id=device.id,
                        card_id=card.id,
                        processed=False,
                    )
                    .order_by(CardAction.created)  # asc
                )
            )
            .scalars()
            .all()
        )

        if device.usecase == "C":
            min, sec = sum_times(last_actions)
            response = DeviceCardResponse(
                CUSTOMERNAME="",
                CUSTOMERSTARTSTOP="",
                ICON="HI",
                STATE="END",
                UNITS=f"{min}:{sec}",
                ERROR="",
                DEVUSECASE="C",
            )
            print(response.model_dump_json())
            sock.sendall(response.model_dump_json().encode("utf-8"))
            return

        elif device.usecase == "G":
            response = DeviceCardResponse(
                CUSTOMERNAME="",
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
