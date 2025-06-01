from typing import List, Optional
from sqlalchemy import DateTime, ForeignKey, Sequence
from sqlalchemy.sql import func
from sqlalchemy.orm import Mapped, mapped_column, relationship
from database import db
from datetime import datetime


class Device(db.Model):
    __tablename__ = "ws_device"
    id: Mapped[int] = mapped_column(primary_key=True)
    mac: Mapped[str] = mapped_column(server_default="")
    name: Mapped[str] = mapped_column(server_default="")
    usecase: Mapped[str] = mapped_column(server_default="")
    terminal: Mapped[str] = mapped_column(server_default="")
    card_actions: Mapped[List["CardAction"]] = relationship(back_populates="device")

    def __repr__(self):
        return self.name


class Card(db.Model):
    __tablename__ = "ws_card"
    id: Mapped[int] = mapped_column(primary_key=True)
    rfid: Mapped[str]
    name: Mapped[str]
    card_actions: Mapped[List["CardAction"]] = relationship(back_populates="card")

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

    def get_last_actions(card: Card):
        if card is None:
            return []
        card_actions: Sequence[CardAction] = (
            (
                db.session.execute(
                    db.select(CardAction)
                    .filter_by(
                        card_id=card.id,
                        processed=False,
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
        clip: Clipboard = (
            db.session.execute(db.select(Clipboard).filter_by(terminal=terminal))
        ).scalar_one_or_none()
        if clip is None:
            return ""
        return clip.rfid

    def set_rfid(terminal: str, rfid: str):
        new_clipboard = Clipboard(terminal=terminal, rfid=rfid)
        db.session.add(new_clipboard)
        db.session.commit()
