let stompClient = null;

function connectWebSocket() {
  const socket = new SockJS('/ws');
  stompClient = Stomp.over(socket);
  stompClient.connect({}, function (frame) {
    stompClient.subscribe('/message/comment', function (response) {
      const comment = JSON.parse(response.body);
      if (comment.threadId == threadId) {
        createComment(comment);
      }
    });
  });
}

$("#commentForm").on("submit", function(event) {
  event.preventDefault();
  const comment = $('#comment').val();
  if (comment != "") {
    stompClient.send("/app/comment", {}, JSON.stringify({ comment, threadId }));
    $('#comment').val('');
  }
});

function createComment(comment) {
  const messageContent = document.getElementById("message-content");
  const newComment = document.createElement("div");
  newComment.className = `d-flex mb-3 ${comment.createdBy == 0 ? 'justify-content-start' : 'justify-content-end'}`;
  newComment.innerHTML = `
    <div class="w-75">
      <div id="comment_${comment.id}" class="card ${comment.status == '100' ? 'unconfirmed-user' : ''}">
        <div class="card-body">
          <span class="card-text">${comment.comment}</span>
        </div>
      </div>
      <div class="py-0 ${comment.createdBy == 0 ? 'text-start' : 'text-end'}">
        <small>${formatDateTime(comment.createdAt)}</small>
      </div>
    </div>
  `;
  messageContent.querySelector(".toast-body").appendChild(newComment);
  showMessage(comment.id);
}

function showMessage(commentId = 0) {
  const toast = new bootstrap.Toast(document.getElementById("message-content"));
  toast.show();
  if (commentId !== 0) {
    const selectElem = document.getElementById('comment_' + commentId);
    if (selectElem !== null) {
      selectElem.scrollIntoView({ behavior: 'smooth', block: 'start' });
      stompClient.send("/app/confirmed", {}, JSON.stringify({ threadId }));
      return;
    }
  }
  const cardElem = document.getElementsByClassName('card');
  cardElem[cardElem.length - 1].scrollIntoView({ behavior: 'smooth', block: 'start' });

  stompClient.send("/app/confirmed", {}, JSON.stringify({ threadId }));
}

connectWebSocket();

function formatDateTime(dateTime) {
  const date = new Date(dateTime);
  const options = {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    hour12: false
  };
  return date.toLocaleDateString('en-US', options).replace(',', '');
}
