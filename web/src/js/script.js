// Coleta de elementos
const tabButtons = document.querySelectorAll('.tab-btn');
const tabContents = document.querySelectorAll('.tab-content');
const newBookBtn = document.getElementById('new-book-btn');
const newReaderBtn = document.getElementById('new-reader-btn');
const newLoanBtn = document.getElementById('new-loan-btn');
const bookFormContainer = document.getElementById('book-form-container');
const readerFormContainer = document.getElementById('reader-form-container');
const loanFormContainer = document.getElementById('loan-form-container');
const modalCloseButtons = document.querySelectorAll('.modal .close');
const modals = document.querySelectorAll('.modal');

// Alterar entre abas
tabButtons.forEach(function (btn) {
    btn.addEventListener('click', function () {
        tabButtons.forEach(b => b.classList.remove('active'));
        tabContents.forEach(tc => tc.classList.remove('active'));

        const targetContent = document.getElementById(btn.dataset.tab);
        if (targetContent) {
            btn.classList.add('active');
            targetContent.classList.add('active');

            switch (btn.dataset.tab) {
                case "books-tab":
                    fetchBooks();
                    break;
                case "readers-tab":
                    fetchReaders();
                    break;
                case "loans-tab":
                    fetchLoan();
                    break;
            }
        }
    });
});

// Abrir pop-up (modais)
if (newBookBtn && bookFormContainer) {
    newBookBtn.addEventListener('click', function () {
        bookFormContainer.style.display = 'flex';
        document.body.classList.add('modal-open');
    });
}

if (newReaderBtn && readerFormContainer) {
    newReaderBtn.addEventListener('click', function () {
        readerFormContainer.style.display = 'flex';
        document.body.classList.add('modal-open');
    });
}

if (newLoanBtn && loanFormContainer) {
    newLoanBtn.addEventListener('click', function () {
        loanFormContainer.style.display = 'flex';
        document.body.classList.add('modal-open');
    });
}

// Fechar pop-up (modais)
modalCloseButtons.forEach(function (closeBtn) {
    closeBtn.addEventListener('click', function () {
        const modal = closeBtn.closest('.modal');
        if (modal) {
            modal.style.display = 'none';
            document.body.classList.remove('modal-open');
        }
    });
});

// Fechar pop-up ao clicar fora do conteúdo
window.addEventListener('click', function (e) {
    modals.forEach(function (modal) {
        if (e.target === modal) {
            modal.style.display = 'none';
            document.body.classList.remove('modal-open');
        }
    });
});

function fetchLoan() {
    fetch("http://localhost:8080/loan")
        .then(response => response.json())
        .then(data => {
            const tableLoan = document.getElementById("loans-list")
            tableLoan.innerHTML = '';
            data.forEach(loan => {
                tableLoan.innerHTML += `
                    <tr>
                        <td>${loan.bookTitle}</td>
                        <td>${loan.readerName}</td>
                        <td>${loan.dateLoan}</td>
                        <td>${loan.returnDate}</td>
                        <td>${loan.status}</td>
                        <td>
                            <button onclick="deleteLoan(${loan.id})">Devolver</button>
                        </td>
                    </tr>
                `
            })
        })
        .catch(err => console.error("Erro ao buscar Emprestimos: ", err))
}

function saveLoan(event) {
    event.preventDefault();

    const bookSelect = document.getElementById("book-select");
    const readerSelect = document.getElementById("reader-select");
    const dueDate = document.getElementById("due-date");

    const bookTitle = bookSelect.options[bookSelect.selectedIndex].text;
    const readerName = readerSelect.options[readerSelect.selectedIndex].text;

    const returnDateInput = document.getElementById("due-date").value;
    const returnDate = formatDateToBr(returnDateInput);
    const dateLoan = formatDateToBr(new Date().toISOString().split('T')[0]);

    const loanData = {
        id: Math.floor(Math.random() * 1000),
        bookTitle,
        readerName,
        dateLoan: dateLoan,
        returnDate,
        status: "EM_DIA"
    };

    console.log(loanData)
    fetch('http://localhost:8080/loan', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(loanData),
    })
        .then(response => response.json())
        .then(data => {
            alert('Empréstimo realizado com sucesso!');
            closeModalLoan();
            fetchLoan();
        })
        .catch(err => console.error("Erro ao salvar empréstimo:", err));
}

function formatDateToBr(dateStr) {
    const [year, month, day] = dateStr.split("-");
    return `${day}/${month}/${year}`;
}

if (newLoanBtn && loanFormContainer) {
    newLoanBtn.addEventListener('click', function () {
        fetchBooksForLoan();
        fetchReadersForLoan();

        loanFormContainer.style.display = 'flex';
        document.body.classList.add('modal-open');
    });
}

function fetchBooksForLoan() {
    fetch("http://localhost:8080/books")
        .then(response => response.json())
        .then(data => {
            const bookSelect = document.getElementById("book-select");
            bookSelect.innerHTML = '';
            data.forEach(book => {
                bookSelect.innerHTML += `
                    <option value="${book.id}">${book.title}</option>
                `;
            });
        })
        .catch(err => console.error("Erro ao buscar livros: ", err));
}

function fetchReadersForLoan() {
    fetch("http://localhost:8080/users")
        .then(response => response.json())
        .then(data => {
            const readerSelect = document.getElementById("reader-select");
            readerSelect.innerHTML = '';
            data.forEach(reader => {
                readerSelect.innerHTML += `
                    <option value="${reader.id}">${reader.name}</option>
                `;
            });
        })
        .catch(err => console.error("Erro ao buscar leitores: ", err));
}

function deleteLoan(loanId) {
    if (confirm("Tem certeza que deseja devolver este livro?")) {
        fetch(`http://localhost:8080/loan/${loanId}`, {
            method: 'DELETE',
        })
            .then(() => {
                alert("Empréstimo devolvido com sucesso.");
                fetchLoan();
            })
            .catch(err => console.error("Erro ao devolver empréstimo:", err))
    }
}

function closeModalLoan() {
    const loanFormContainer = document.getElementById("loan-form-container");
    loanFormContainer.style.display = 'none';
    document.body.classList.remove('modal-open');
}

let booksList = [];
function fetchBooks() {
    fetch("http://localhost:8080/books")
        .then(response => response.json())
        .then(data => {
            booksList = data;
            const tableBooks = document.getElementById("books-list")
            tableBooks.innerHTML = '';

            data.forEach(book => {
                tableBooks.innerHTML += `
                    <tr>
                        <td>${book.title}</td>                
                        <td>${book.author}</td>                
                        <td>${book.category}</td>                
                        <td id="book-status">${book.status}</td>                
                        <td>
                            <button id="btn-edit-book" onclick="editBook(${book.id})">Editar</button>
                            <button id="btn-delete-book" onclick="deleteBook(${book.id})">Deletar</button>
                        </td>                
                    </tr>      
                `
            })
        })
        .catch(err => console.error("Erro ao buscar livros: ", err))
}

function editBook(bookId) {
    const book = booksList.find(b => b.id === bookId);

    if (book) {
        document.getElementById("book-id").value = book.id;
        document.getElementById("title").value = book.title;
        document.getElementById("author").value = book.author;
        document.getElementById("category").value = book.category;

        const bookFormContainer = document.getElementById("book-form-container");
        bookFormContainer.style.display = 'flex';
        document.body.classList.add('modal-open');
    }
}

function deleteBook(bookId) {
    if (confirm("Tem certeza que deseja deletar este livro?")) {
        fetch(`http://localhost:8080/books/${bookId}`, {
            method: 'DELETE',
        })
            .then(() => {
                alert("Livro deletado com sucesso.");
                fetchBooks();
            })
            .catch(err => console.error("Erro ao deletar livro:", err));
    }
}

function saveEditBook(event) {
    event.preventDefault();

    const bookId = document.getElementById("book-id").value;
    const title = document.getElementById("title").value;
    const author = document.getElementById("author").value;
    const category = document.getElementById("category").value;
    const status = document.getElementById("book-status").innerText;

    const bookData = {
        id: bookId,
        title: title,
        author: author,
        isbn: Math.floor(Math.random() * 10),
        category: category,
        location: "A",
        status: status
    };

    const url = `http://localhost:8080/books/${bookId}`;
    const method = 'PUT'

    fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(bookData),
    })
        .then(response => response.json())
        .then(data => {
            closeModalBook();
            fetchBooks();
        })
        .catch(err => console.error("Erro ao editar livro:", err));
}

function saveBook(event) {
    event.preventDefault();

    const title = document.getElementById("title").value;
    const author = document.getElementById("author").value;
    const category = document.getElementById("category").value;

    const bookData = {
        id: Math.floor(Math.random() * 1000),
        title: title,
        author: author,
        isbn: Math.floor(Math.random() * 100),
        category: category,
        location: "A",
        status: "LIVRE"
    };

    console.log(bookData)

    const url = `http://localhost:8080/books`;
    const method = 'POST'

    fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(bookData),
    })
        .then(response => response.json())
        .then(data => {
            closeModalBook();
            fetchBooks();
        })
        .catch(err => console.error("Erro ao salvar livro:", err));
}

function closeModalBook() {
    const bookFormContainer = document.getElementById("book-form-container");
    bookFormContainer.style.display = 'none';
    document.body.classList.remove('modal-open');
}

function fetchReaders() {
    fetch("http://localhost:8080/users")
        .then(response => response.json())
        .then(data => {
            readersList = data;
            const tableReaders = document.getElementById("readers-list");
            tableReaders.innerHTML = '';

            data.forEach(user => {
                tableReaders.innerHTML += `
                    <tr>
                        <td>${user.name}</td>
                        <td>${user.email}</td>
                        <td>${user.phone}</td>
                        <td>
                            <button onclick="editUser(${user.id})">Editar</button>
                            <button onclick="deleteUser(${user.id})">Deletar</button>
                        </td>
                    </tr>
                `;
            });
        })
        .catch(err => console.error("Erro ao buscar Users: ", err));
}

let readersList = [];
function editUser(userId) {
    const user = readersList.find(u => u.id === userId);

    if (user) {
        document.getElementById("reader-id").value = user.id;
        document.getElementById("reader-name").value = user.name;
        document.getElementById("reader-email").value = user.email;
        document.getElementById("reader-phone").value = user.phone;

        readerFormContainer.style.display = 'flex';
        document.body.classList.add('modal-open');
    }
}

function saveEditUser(event) {
    event.preventDefault();

    const id = document.getElementById("reader-id").value;
    const name = document.getElementById("reader-name").value;
    const email = document.getElementById("reader-email").value;
    const phone = document.getElementById("reader-phone").value;

    const userData = {
        id,
        name,
        email,
        password: Math.floor(Math.random() * 100),
        phone,
        role: "LEITOR"
    };

    fetch(`http://localhost:8080/users/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(userData),
    })
        .then(response => response.json())
        .then(data => {
            closeModalReader();
            fetchReaders();
        })
        .catch(err => console.error("Erro ao editar usuário:", err));
}

function saveUser(event) {
    event.preventDefault();

    const name = document.getElementById("reader-name").value;
    const email = document.getElementById("reader-email").value;
    const phone = document.getElementById("reader-phone").value;

    const userData = {
        id: Math.floor(Math.random() * 1000),
        name,
        email,
        password: Math.floor(Math.random() * 100),
        phone,
        role: "LEITOR"
    };

    fetch(`http://localhost:8080/users`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(userData),
    })
        .then(response => response.json())
        .then(data => {
            closeModalReader();
            fetchReaders();
        })
        .catch(err => console.error("Erro ao salvar usuário:", err));
}

function deleteUser(userId) {
    if (confirm("Tem certeza que deseja deletar este usuário?")) {
        fetch(`http://localhost:8080/users/${userId}`, {
            method: 'DELETE',
        })
            .then(() => {
                alert("Usuário deletado com sucesso.");
                fetchReaders();
            })
            .catch(err => console.error("Erro ao deletar usuário:", err));
    }
}

function closeModalReader() {
    const readerFormContainer = document.getElementById("reader-form-container");
    readerFormContainer.style.display = 'none';
    document.body.classList.remove('modal-open');
}

document.addEventListener('DOMContentLoaded', function () {
    const firstTab = document.querySelector('.tab-btn[data-tab="books"]');
    if (firstTab) {
        fetchBooks()
    }

    const tabButtons = document.querySelectorAll('.tab-btn');
    const tabContents = document.querySelectorAll('.tab-content');

    tabButtons.forEach(function (btn) {
        btn.addEventListener('click', function () {
            tabButtons.forEach(b => b.classList.remove('active'));
            tabContents.forEach(tc => tc.classList.remove('active'));

            const targetContent = document.getElementById(btn.dataset.tab);
            if (targetContent) {
                btn.classList.add('active');
                targetContent.classList.add('active');

                switch (btn.dataset.tab) {
                    case "books":
                        fetchBooks();
                        break;
                    case "readers":
                        fetchReaders();
                        break;
                    case "loans":
                        fetchLoan();
                        break;
                }
            }
        });
    });

    const loanForm = document.getElementById("loan-form");
    if (loanForm) {
        loanForm.addEventListener('submit', saveLoan);
    }

    const bookForm = document.getElementById("book-form");
    if (bookForm) {
        bookForm.addEventListener('submit', function (event) {
            event.preventDefault();
            const bookId = document.getElementById("book-id").value;
            bookId ? saveEditBook(event) : saveBook(event);
        });
    }

    const readerForm = document.getElementById("reader-form");
    if (readerForm) {
        readerForm.addEventListener('submit', function (event) {
            event.preventDefault();
            const readerId = document.getElementById("reader-id").value;
            readerId ? saveEditUser(event) : saveUser(event);
        });
    }

    const bookFormContainer = document.getElementById("book-form-container");
    const bookModalClose = document.querySelector('.close');
    if (bookModalClose) {
        bookModalClose.addEventListener('click', closeModalBook);
    }

    const readerFormContainer = document.getElementById("reader-form-container");
    const readerModalClose = document.getElementById("close-reader-modal");
    if (readerModalClose) {
        readerModalClose.addEventListener('click', closeModalReader);
    }

    window.addEventListener('click', function (event) {
        if (event.target === bookFormContainer) closeModalBook();
        if (event.target === readerFormContainer) closeModalReader();
    });
});
