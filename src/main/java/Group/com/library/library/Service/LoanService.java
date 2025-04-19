package Group.com.library.library.Service;

import Group.com.library.library.Enum.BookStatusEnum;
import Group.com.library.library.Enum.LoanStatusEnum;
import Group.com.library.library.Exception.LivroIndisponivelException;
import Group.com.library.library.Model.Book;
import Group.com.library.library.Model.Loan;
import Group.com.library.library.Model.User;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class LoanService {

    private final BookService bookService;
    private final UserService userService;
    private final String LOAN_FILE_PATH = "src/database/loans.csv";
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public LoanService(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
    }

    public List<Loan> index() throws IOException {
        List<Loan> loans = new ArrayList<>();
        List<Book> books = bookService.index();
        List<User> users = userService.index();

        try (CSVReader reader = new CSVReader(new FileReader(LOAN_FILE_PATH))) {
            String[] line;
            boolean isHeader = true;

            while ((line = reader.readNext()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                if (line.length < 6 || line[0].isEmpty()) continue;

                int loanId = Integer.parseInt(line[0]);
                Date loanDate = sdf.parse(line[1]);
                Date returnDate = sdf.parse(line[2]);
                LoanStatusEnum status = LoanStatusEnum.valueOf(line[3]);

                String bookTitle = line[4];
                Book book = books.stream()
                        .filter(b -> b.getTitle().equalsIgnoreCase(bookTitle))
                        .findFirst()
                        .orElse(null);

                String userName = line[5];
                User user = users.stream()
                        .filter(u -> u.getName().equalsIgnoreCase(userName))
                        .findFirst()
                        .orElse(null);

                if (book != null && user != null) {
                    Loan loan = new Loan(
                            loanId,
                            book.getTitle(),
                            user.getName(),
                            loanDate,
                            returnDate,
                            status
                    );
                    loans.add(loan);
                }
            }
        } catch (Exception e) {
            throw new IOException("Erro ao carregar empréstimos.", e);
        }

        return loans;
    }

    public Loan create(@RequestBody Loan loan) throws IOException, LivroIndisponivelException {
        File file = new File(LOAN_FILE_PATH);
        boolean fileExists = file.exists();

        if (loan.getDateLoan() == null || loan.getReturnDate() == null) {
            throw new IllegalArgumentException("As datas não podem ser nulas.");
        }

        List<Book> books = bookService.index();
        List<User> users = userService.index();

        Book book = books.stream()
                .filter(b -> Objects.equals(b.getTitle(), loan.getBookTitle()))
                .findFirst()
                .orElse(null);

        if (book == null) throw new RuntimeException("Livro não encontrado.");

        if (book.getStatus() == BookStatusEnum.EMPRESTADO || book.getStatus() == BookStatusEnum.RESERVADO) {
            throw new LivroIndisponivelException("Livro indisponível.");
        }

        User user = users.stream()
                .filter(u -> Objects.equals(u.getName(), loan.getReaderName()))
                .findFirst()
                .orElse(null);

        if (user == null) throw new RuntimeException("Usuário não encontrado.");

        bookService.updateBookStatus(book.getTitle(), BookStatusEnum.EMPRESTADO);

        try (CSVWriter writer = new CSVWriter(new FileWriter(LOAN_FILE_PATH, true))) {
            if (!fileExists) {
                writer.writeNext(new String[]{"ID", "DateLoan", "ReturnDate", "Status", "BookTitle", "ReaderName"});
            }

            writer.writeNext(new String[]{
                    String.valueOf(loan.getId()),
                    sdf.format(loan.getDateLoan()),
                    sdf.format(loan.getReturnDate()),
                    loan.getStatus().name(),
                    String.valueOf(loan.getBookTitle()),
                    String.valueOf(loan.getReaderName())
            });
        }

        return loan;
    }

    public ResponseEntity<Loan> update(@PathVariable int id, @RequestBody Loan updatedLoan) throws IOException {
        List<Loan> loans = index();
        boolean updated = false;

        Loan existingLoan = loans.stream().filter(l -> l.getId() == id).findFirst().orElse(null);
        if (existingLoan == null) throw new RuntimeException("Empréstimo não encontrado.");

        bookService.updateBookStatus(existingLoan.getBookTitle(), BookStatusEnum.LIVRE);

        bookService.updateBookStatus(updatedLoan.getBookTitle(), BookStatusEnum.EMPRESTADO);

        for (Loan loan : loans) {
            if (loan.getId() == id) {
                loan.setDateLoan(updatedLoan.getDateLoan());
                loan.setReturnDate(updatedLoan.getReturnDate());
                loan.setStatus(updatedLoan.getStatus());
                loan.setBookTitle(updatedLoan.getBookTitle());
                loan.setReaderName(updatedLoan.getReaderName());
                updated = true;
                break;
            }
        }

        if (!updated) throw new RuntimeException("Erro ao atualizar empréstimo.");

        try (CSVWriter writer = new CSVWriter(new FileWriter(LOAN_FILE_PATH, false))) {
            writer.writeNext(new String[]{"ID", "DateLoan", "ReturnDate", "Status", "BookTitle", "ReaderName"});
            for (Loan loan : loans) {
                writer.writeNext(new String[]{
                        String.valueOf(loan.getId()),
                        sdf.format(loan.getDateLoan()),
                        sdf.format(loan.getReturnDate()),
                        loan.getStatus().name(),
                        String.valueOf(loan.getBookTitle()),
                        String.valueOf(loan.getReaderName())
                });
            }
        }

        return ResponseEntity.ok(updatedLoan);
    }

    public ResponseEntity<Loan> delete(@PathVariable int id) throws IOException {
        List<Loan> loans = index();
        Loan loanToDelete = loans.stream().filter(l -> l.getId() == id).findFirst().orElse(null);

        if (loanToDelete == null) throw new RuntimeException("Empréstimo não encontrado.");

        bookService.updateBookStatus(loanToDelete.getBookTitle(), BookStatusEnum.LIVRE);

        loans.removeIf(l -> l.getId() == id);

        try (CSVWriter writer = new CSVWriter(new FileWriter(LOAN_FILE_PATH, false))) {
            writer.writeNext(new String[]{"ID", "DateLoan", "ReturnDate", "Status", "BookTitle", "ReaderName"});
            for (Loan loan : loans) {
                writer.writeNext(new String[]{
                        String.valueOf(loan.getId()),
                        sdf.format(loan.getDateLoan()),
                        sdf.format(loan.getReturnDate()),
                        loan.getStatus().name(),
                        String.valueOf(loan.getBookTitle()),
                        String.valueOf(loan.getReaderName())
                });
            }
        }

        return ResponseEntity.ok().build();
    }
}
