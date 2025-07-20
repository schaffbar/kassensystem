from typing import Type

from pydantic import BaseModel
from models import CardAction


def sum_times(card_actions: list[CardAction]) -> tuple[int, int]:
    """
    sum up all the time deltas in between card actions

    returns (minutes, seconds)

    TODO: handle multiple checkins in a row
    """
    times = [card_action.created for card_action in card_actions]
    time_pairs = zip(times[::2], times[1::2])
    seconds = sum([(p[1] - p[0]).seconds for p in time_pairs])
    return divmod(seconds, 60)


def send_response(response: Type[BaseModel]):
    return response.model_dump_json(), 200, {"Content-Type": "application/json"}


def create_balancing_record(action, device, card):
    # wenn durch einen Neustart des rfidReaders keine Abmeldung möglich ist (im UseCase: SwitchBox)
    # oder der Kunde vergisst für eine Pause auszuchecken (im UseCase: GateKeeper)
    bal_action = CardAction(
        device=device,
        card=card,
        type=action,
        processed=False,
    )
    db.session.add(bal_action)
    db.session.commit()
