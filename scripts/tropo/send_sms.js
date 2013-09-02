// arguments:
//  destination: the phone number to dial
//  content: the message to send

function parseNumber (pn) {

    // keep only digits
    var digits = pn.replace( /[^\d]/g, '' );

    // if 10 digits long, prepend +1
    if (digits.length == 10) return "+1" + digits;

    // if 11 digits long, prepend +
    if (digits.length == 11) return "+" + digits;

    return "invalid-phone:"+pn;
}

var pn = parseNumber(destination);
var event = call(pn, {network:"SMS"});

event.value.say(content);
