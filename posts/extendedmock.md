# (2024) Extending pytest mocks

IMHO, tests (+ code in general) should read cleanly and minimize cognitive load.

One small place this breaks down is when checking the arguments passed to a mock.
`unittest.mock` already tracks everything, but it keeps positional and keyword
arguments in separate places:

- positional arguments live in `mock.call_args.args`
- keyword arguments live in `mock.call_args.kwargs`

That is useful, but it can make assertions reflect the mechanics of the call
instead of the behavior being tested.

For example:

```py
def charge_customer(
    customer_id: str,
    amount_cents: int,
    *,
    currency: str,
    metadata: dict[str, str],
) -> None:
    ...


def checkout(order) -> None:
    charge_customer(
        order.customer_id,
        order.total_cents,
        currency="USD",
        metadata={"order_id": order.id, "source": "checkout"},
    )
```

If `checkout` calls `charge_customer`, a test without any helper might look like
this:

```py
def test_checkout_charges_customer(charge_customer_mock):
    order = Order(id="ord_123", customer_id="cus_123", total_cents=4999)

    checkout(order)

    charge_customer_mock.assert_called_once()

    args = charge_customer_mock.call_args.args
    kwargs = charge_customer_mock.call_args.kwargs

    assert args[0] == "cus_123"  # customer_id
    assert args[1] == 4999  # amount_cents
    assert kwargs["currency"] == "USD"
    assert kwargs["metadata"]["source"] == "checkout"
```

This works, but it forces the reader to decode indexes. `args[0]` only means
`customer_id` if you already know the function signature. The comments help,
but they also point to the problem.

I would rather write the relevant assertions using the function's parameter
names:

```py
    assert charge_customer_mock.call_dict_contains(
        customer_id="cus_123",
        amount_cents=4999,
        currency="USD",
    )
    assert charge_customer_mock.call_dict["metadata"]["source"] == "checkout"
```

Now the assertion uses the parameter names of the function being called instead
of the structure of `mock.call_args`.

The helper is small:

<details markdown="1">
<summary>ExtendedMock implementation</summary>

```py
import inspect
from functools import cached_property
from typing import Any
from unittest.mock import MagicMock


class ExtendedMock(MagicMock):
    """
    ExtendedMock adds two convenience helpers:
    - `call_dict` maps the last call's arguments to parameter names
    - `call_dict_contains` checks whether expected key/value pairs were passed
    """

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        object.__setattr__(self, "_spec_func", kwargs.get("spec"))

    @cached_property
    def signature(self) -> inspect.Signature:
        return inspect.signature(self._spec_func)

    @cached_property
    def params(self) -> tuple[str, ...]:
        return tuple(self.signature.parameters)

    @property
    def call_dict(self) -> dict[str, Any]:
        positional_args = dict(zip(self.params, self.call_args.args))
        return {**positional_args, **self.call_args.kwargs}

    def call_dict_contains(self, **kwargs) -> bool:
        actual = self.call_dict
        return all(
            key in actual and actual[key] == value
            for key, value in kwargs.items()
        )
```

</details>

Usage looks like this:

```py
from unittest import mock


@mock.patch("payments.checkout.charge_customer", new_callable=ExtendedMock, spec=True)
def test_checkout_charges_customer(charge_customer_mock):
    order = Order(id="ord_123", customer_id="cus_123", total_cents=4999)

    checkout(order)

    charge_customer_mock.assert_called_once()
    assert charge_customer_mock.call_dict_contains(
        customer_id=order.customer_id,
        amount_cents=order.total_cents,
        currency="USD",
    )
```

The important bit is `spec=True`. The helper uses the patched function's
signature to map positional arguments back to parameter names.

This does not replace `assert_called_with`, autospeccing, or anything else in
`unittest.mock`. It is just a small helper for cases where I want to check a few
business-level values without translating from `args[0]` first.
