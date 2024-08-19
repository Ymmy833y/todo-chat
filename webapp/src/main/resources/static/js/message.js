
$("#sendCommentForm").on("submit", function(event) {
  event.preventDefault();
  const comment = $('#comment').val();
  if (comment != "") {
    const csrfToken = $("#sendCommentForm input[name='_csrf']").val();

    createComment({ comment: comment, createdBy: threadId, createdAt: new Date(), status: 200 });
    $("#comment").val("");
    $("#sendBtn").prop("disabled", true);

    fetch('/comment/send', {
      method: 'POST',
      headers: {
          'Content-Type': 'application/json',
          'X-CSRF-TOKEN': csrfToken
      },
      body: JSON.stringify({ comment, threadId })
    })
    .then(response => response.json())
    .then(comment => {
      $("#sendBtn").prop("disabled", false);
      createComment(comment);
    })
    .catch(error => {
      $("#sendBtn").prop("disabled", false);
      console.error('Error:', error);
    });
  }
});

function createComment(comment) {
  const messageContent = document.getElementById("message-content");
  const newComment = document.createElement("div");
  newComment.className = `d-flex mb-3 ${comment.createdBy == 0 ? 'justify-content-start' : 'justify-content-end'}`;
  newComment.innerHTML = `
    <div class="w-75">
      <div id="comment_${comment.id}" class="card ${comment.status == '100' ? 'unconfirmed-user' : ''}">
        <div class="card-body comment">${comment.comment}</div>
      </div>
      <div class="py-0 ${comment.createdBy == 0 ? 'text-start' : 'text-end'}">
        <small>${formatDateTime(comment.createdAt)}</small>
      </div>
    </div>
  `;
  messageContent.querySelector(".toast-body").appendChild(newComment);
  convertMD2HTML(".comment");
  showMessage(comment.id);
}

function showMessage(commentId = 0) {
  const toast = new bootstrap.Toast(document.getElementById("message-content"));
  toast.show();
  if (commentId !== 0) {
    const selectElem = document.getElementById('comment_' + commentId);
    if (selectElem !== null) {
      selectElem.scrollIntoView({ behavior: 'smooth', block: 'start' });
      return;
    }
  }
  const cardElem = document.getElementsByClassName('card');
  cardElem[cardElem.length - 1].scrollIntoView({ behavior: 'smooth', block: 'start' });

  confirmed();
}

function confirmed() {
  const csrfToken = $("#sendCommentForm input[name='_csrf']").val();
  fetch('/comment/confirmed', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
        'X-CSRF-TOKEN': csrfToken
    },
    body: JSON.stringify({ threadId })
  })
  .catch(error => console.error('Error:', error));
}

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
