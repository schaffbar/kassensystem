from typing import List
from sqlalchemy import DateTime, ForeignKey
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
