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
        tabButtons.forEach(function (b) {
            b.classList.remove('active');
        });
        tabContents.forEach(function (tc) {
            tc.classList.remove('active');
        });

        const targetContent = document.getElementById(btn.dataset.tab);
        if (targetContent) {
            btn.classList.add('active');
            targetContent.classList.add('active');
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
