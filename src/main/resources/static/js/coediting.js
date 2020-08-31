var stompClient = null;
var isConnected = false;

function setConnected(status) {
    this.isConnected = status;
    $('#live').prop("checked", status);
}

function switchConnected() {
    if (isConnected) {
        disconnect();
    } else {
        connect();
    }
}

function connect() {
    var socket = new SockJS('/note');
    var noteId = $("#id").val();
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        stompClient.subscribe('/topic/note/' + noteId, function (note) {
            showMessageOutput(JSON.parse(note.body));
        });
        sendMessage();
    });
}

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendMessage() {
    var id = $("#id").val();
    var title = $("#title").val();
    var content = $("#content").val();
    stompClient.send("/socket/note/" + id, {},
        JSON.stringify({'id': id, 'title': title, 'content': content}));
    autosaveRandomly();
}

// autosaves a note 1 in 20 times
function autosaveRandomly() {
    var random = getRandomInt(20);
    if (random === 8) {
        autosave();
    }
}

function autosave() {
    let wasConnected = this.isConnected;
    if (!wasConnected) {
        switchConnected();
    }

    var id = $("#id").val();
    var title = $("#title").val();
    var content = $("#content").val();
    stompClient.send("/socket/note/" + id + "/autosave", {},
        JSON.stringify({'id': id, 'title': title, 'content': content}));

    $("#autosaved").show('slow').delay(800).hide(0);

    if (!wasConnected) {
        switchConnected();
    }
}

function showMessageOutput(note) {
    $("#title").val(note.title);
    $("#content").val(note.content);
}

function getRandomInt(max) {
    return Math.floor(Math.random() * Math.floor(max));
}

function setOSSpecificSavePopoverMessage() {
    let saveButton = $("#submitSaveButton");

    // change shortcut for Mac
    if (navigator.appVersion.indexOf("Mac")!=-1) {
        var text = saveButton.attr("data-content");
        text.replace("ctrl + s", "⌘ + s");
        saveButton.attr("data-content", text);
    }

}
$("#title").on('input', function () {
    sendMessage();
});

$("#content").on('input', function () {
    sendMessage();
});

$("#submitSaveButton").hover(function () {
    $("#submitSaveButton").popover('toggle');
}, function () {
    $("#submitSaveButton").popover('toggle');
});

$(document).ready(function() {
    setOSSpecificSavePopoverMessage()
});


// ctrl+s/cmd+s save shortcut processing
$(document).keydown(function(e) {

    var key = undefined;
    var possible = [ e.key, e.keyIdentifier, e.keyCode, e.which ];

    while (key === undefined && possible.length > 0)
    {
        key = possible.pop();
    }

    if (key && (key == '115' || key == '83' ) && (e.ctrlKey || e.metaKey) && !(e.altKey))
    {
        e.preventDefault();
        autosave();
        return false;
    }
    return true;
});