from typing import List, Optional
from sqlalchemy import DateTime, ForeignKey, Sequence, Integer
from sqlalchemy.sql import func
from sqlalchemy.orm import Mapped, mapped_column, relationship
from database import db
from datetime import datetime

class Tools(db.Model):                                                                              # <- komplette Klasse/Struktur für die Shellys eingefügt
    __tablename__ = "ws_solenoidtool"                                 
    id           : Mapped[int] = mapped_column(primary_key=True)
    name         : Mapped[str] = mapped_column(server_default="")
    httpstartstr : Mapped[str] = mapped_column(server_default="")
    ipaddrstr    : Mapped[str] = mapped_column(server_default="")
    onstr        : Mapped[str] = mapped_column(server_default="")
    offstr       : Mapped[str] = mapped_column(server_default="")
    productid    : Mapped[str] = mapped_column(server_default="")                                   # <- Für die Zuordnung der aufsummierten Zeiten zu einer Artikelnummer dieses Paar kann dann im Warenkorb abgelegt werden.
    gateflag     : Mapped[str] = mapped_column(server_default="")                                   # <- Im UseCase = GateKeeper kann die Anwesenheitzeit de Kunden in der Werkstatt so einer Artikelnummer für de Warenkorb zugeordnet werden.
    def __repr__(self):
        return self.name

class Device(db.Model):
    __tablename__ = "ws_device"
    id           : Mapped[int] = mapped_column(primary_key=True)
    mac          : Mapped[str] = mapped_column(server_default="")
    name         : Mapped[str] = mapped_column(server_default="")
    usecase      : Mapped[str] = mapped_column(server_default="")
    terminal     : Mapped[str] = mapped_column(server_default="")
    toolid       : Mapped[Optional["Tools"]] = mapped_column(ForeignKey("ws_solenoidtool.id"),nullable=True)                     # <- Id aus der Tabelle ws_solenoidtool
    card_actions : Mapped[List["CardAction"]] = relationship(back_populates="device")
    def __repr__(self):
        return self.name

class Customers(db.Model):                                                                          # <- komplette Klasse/Struktur für die Kunden eingefügt
    __tablename__ = "ws_kunden"                                                                     #    diese wird später durch die Tabelle res.partner ersetzt
    id           : Mapped[int] = mapped_column(primary_key=True)                                    #    wird aber benötigt für die Freigabe der einzelnen Werkzeuge
    vorname      : Mapped[str] = mapped_column(server_default="")
    nachname     : Mapped[str] = mapped_column(server_default="")
    strasse      : Mapped[str] = mapped_column(server_default="")
    plz          : Mapped[str] = mapped_column(server_default="")
    ort          : Mapped[str] = mapped_column(server_default="")
    email        : Mapped[str] = mapped_column(server_default="")
    taghandover  : Mapped[str] = mapped_column(server_default="")                                   # <- temporäre [T] oder permanente [P] Übergabe des rfidTags an den Kunden 

class Certificates(db.Model):                                                                       # <- Tabelle mit den Zuordnungen für die Freigaben der einzelnen
    __tablename__ = "ws_certificates"                                                               #    Werkzeugen für einzelne Kunden
    id           : Mapped[int] = mapped_column(primary_key=True)                                    # <- muss anscheinend defineirt sein, ohne prim kery gab es eine Fehlermeldung
    customerid                 = mapped_column(ForeignKey("ws_kunden.id")) 
    toolid                     = mapped_column(ForeignKey("ws_solenoidtool.id"))

class Card(db.Model):
    __tablename__ = "ws_card"
    id: Mapped[int]            = mapped_column(primary_key=True)
    rfid: Mapped[str]
    customerid                 = mapped_column(ForeignKey("ws_kunden.id"))                          # <- der Karte wird ein Kunde aus der Kunden-Tabelle zugeordnet
    name: Mapped[str]
    card_actions: Mapped[List["CardAction"]] = relationship(back_populates="card")                  # <- Ist die Liste hier notwendig

    def __repr__(self):
        return self.name

    def set_name(rfid: str, name: str):  
        card: Optional[Card] = (
            db.session.execute(db.select(Card).filter_by(rfid=rfid))
        ).scalar_one_or_none()

        if not card:
            return

        card.name = name
        db.session.commit()

    def get_by_rfid(rfid: str):
        card: Optional[Card] = (
            db.session.execute(db.select(Card).filter_by(rfid=rfid))
        ).scalar_one_or_none()
        return card


class CardAction(db.Model):
    __tablename__ = "ws_card_action"
    id: Mapped[int] = mapped_column(primary_key=True)
    device_id: Mapped[int] = mapped_column(ForeignKey("ws_device.id"))
    device: Mapped["Device"] = relationship(back_populates="card_actions")
    card_id: Mapped[int] = mapped_column(ForeignKey("ws_card.id"))
    card: Mapped["Card"] = relationship(back_populates="card_actions")
    type: Mapped[str]  # checkin, checkout
    processed: Mapped[bool] = mapped_column(server_default="f")
    created: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), server_default=func.now(), index=True
    )

# Diese Funktion wird durch die folgende ersetzt,
# dadurch funktioniert die aktuelle Abrechnung nicht mehr
#    def get_last_actions(card: Card):
#        if card is None:
#            return []
#        card_actions: Sequence[CardAction] = (
#            (
#                db.session.execute(
#                    db.select(CardAction)
#                    .filter_by(
#                        card_id=card.id,
#                        processed=False,
#                    )
#                    .order_by(CardAction.created)  # asc
#                )
#            )
#            .scalars()
#            .all()
#        )
#        return card_actions
    
    def get_last_actions(card: Card, device: Device):
        # Erstellung der Action-Liste in Abhängigkeit des 
        # Kunden und des rfidReader für die spätere Zuordnung der Artikel im Warenkorb
        
        # wenn Karte oder Device fehlt gebe eine leere Liste zurück
        if card is None:
            return []
        if device is None:
            return []
        card_actions : Sequence[CardAction] = (
            (
                db.session.execute(
                    db.select(CardAction)
                    .filter_by(
                        card_id   = card.id,
                        device_id = device.id,
                        processed = False,
                    )
                    .order_by(CardAction.created)  # asc
                )
            )
            .scalars()
            .all()
        )
        return card_actions
    
    def mark_as_processed(rfid: str):
        card = Card.get_by_rfid(rfid)
        if card is None:
            return

        card_actions = CardAction.get_last_actions(rfid)
        for action in card_actions:
            action.processed = True
        db.session.commit()


class Clipboard(db.Model):
    __tablename__ = "ws_clipboard"
    id: Mapped[int] = mapped_column(primary_key=True)
    terminal: Mapped[str] = mapped_column(unique=True)
    rfid: Mapped[str]

    def get_rfid(terminal: str) -> str:
        clip: Optional[Clipboard] = (
            db.session.execute(db.select(Clipboard).filter_by(terminal=terminal))
        ).scalar_one_or_none()
        if clip is None:
            return ""
        return clip.rfid

    def set_rfid(terminal: str, rfid: str):
        clip: Optional[Clipboard] = (
            db.session.execute(db.select(Clipboard).filter_by(terminal=terminal))
        ).scalar_one_or_none()

        if clip is None:
            new_clipboard = Clipboard(terminal=terminal, rfid=rfid)
            db.session.add(new_clipboard)
        else:
            clip.rfid = rfid

        db.session.commit()
