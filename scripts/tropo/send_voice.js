// arguments:
//  destination: the phone number to dial
//  content: the message to say

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

call(pn, {
    timeout:120,
//    callerID:'14075550100',
    onAnswer: function() {
        say(content);
        log("Call complete");
    },
    onTimeout: function() {
        log("Call timed out");
    },
    onCallFailure: function() {
        log("Call could not be completed as dialed");
    }
});