function post() {
    var questionId = $("#question_id").val();
    var content = $("#comment_content").val();
    window.localStorage.setItem("questionId", questionId);
    $.ajax({
        type: "POST",
        url: "/comment",
        contentType:"application/json",
        data: JSON.stringify({
            "parentId":questionId,
            "content" : content,
            "type" : 1
        }),
        success: function (response) {   //this response is the return value from the comment controller
            if (response.code == 200) {
                window.location.reload(); //refresh the page
            }
            else {
                if (response.code == 2003) {
                    var isAccepted = confirm(response.message);
                    if (isAccepted) {
                        window.open("https://github.com/login/oauth/authorize?client_id=84653d3ce81ad7b7992c&redirect_uri=http://localhost:8080/callback&scope=user&state=1");
                        window.localStorage.setItem("closable", true);

                    }
                }
                else {
                    alert(response.message);
                }
            }
        },
        dataType: "json"
    });
}