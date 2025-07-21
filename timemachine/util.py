from typing import Any, Dict, Optional, Type

from pydantic import BaseModel, ValidationError
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


# Function to validate data against multiple models
def validate_data(
    data: Dict[str, Any], models: list[Type[BaseModel]]
) -> Optional[BaseModel]:
    for model in models:
        try:
            validated_object = model(**data)
            return validated_object
        except ValidationError as e:
            continue
    return None
