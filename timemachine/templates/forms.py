from flask_wtf import FlaskForm
from wtforms import StringField, SubmitField
from wtforms.validators import DataRequired


class RegisterForm(FlaskForm):
    name = StringField("Name", validators=[DataRequired()])
    rfid = StringField("RFID", validators=[DataRequired()])
    submit = SubmitField(label="Anmelden")


class LookupForm(FlaskForm):
    rfid = StringField("RFID", validators=[DataRequired()])
    submit = SubmitField(label="Suchen")


class CheckoutForm(FlaskForm):
    submit = SubmitField(label="Als bezahlt markieren")
