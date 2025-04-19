package Group.com.library.library.Service;

import Group.com.library.library.Enum.BookStatusEnum;
import Group.com.library.library.Enum.LoanStatusEnum;
import Group.com.library.library.Exception.LivroIndisponivelException;
import Group.com.library.library.Model.Book;
import Group.com.library.library.Model.Loan;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanService {

    private final BookService bookService;
    private final String LOAN_FILE_PATH = "src/database/loans.csv";
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public LoanService(BookService bookService) {
        this.bookService = bookService;
    }

    public List<Loan> index() throws IOException {
        List<Loan> loans = new ArrayList<>();
        List<Book> books = bookService.index();

        try (CSVReader reader = new CSVReader(new FileReader(LOAN_FILE_PATH))) {
            String[] line;
            boolean isHeader = true;

            while ((line = reader.readNext()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                if (line.length < 5 || line[0].isEmpty()) continue;

                int bookId = Integer.parseInt(line[4]);
                Book book = books.stream().filter(b -> b.getId() == bookId).findFirst().orElse(null);

                if (book != null) {
                    Loan loan = new Loan(
                            Integer.parseInt(line[0]),
                            book.getId(),
                            sdf.parse(line[1]),
                            sdf.parse(line[2]),
                            LoanStatusEnum.valueOf(line[3])
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
        Book book = books.stream().filter(b -> b.getId() == loan.getBookId()).findFirst().orElse(null);

        if (book == null) throw new RuntimeException("Livro não encontrado.");
        if (book.getStatus() == BookStatusEnum.EMPRESTADO || book.getStatus() == BookStatusEnum.RESERVADO) {
            throw new LivroIndisponivelException("Livro indisponível.");
        }

        bookService.updateBookStatus(book.getId(), BookStatusEnum.EMPRESTADO);

        try (CSVWriter writer = new CSVWriter(new FileWriter(LOAN_FILE_PATH, true))) {
            if (!fileExists) {
                writer.writeNext(new String[]{"ID", "DateLoan", "ReturnDate", "Status", "BookID"});
            }

            writer.writeNext(new String[]{
                    String.valueOf(loan.getId()),
                    sdf.format(loan.getDateLoan()),
                    sdf.format(loan.getReturnDate()),
                    loan.getStatus().name(),
                    String.valueOf(loan.getBookId())
            });
        }

        return loan;
    }

    public ResponseEntity<Loan> update(@PathVariable int id, @RequestBody Loan updatedLoan) throws IOException {
        List<Loan> loans = index();
        boolean updated = false;

        Loan existingLoan = loans.stream().filter(l -> l.getId() == id).findFirst().orElse(null);
        if (existingLoan == null) throw new RuntimeException("Empréstimo não encontrado.");

        bookService.updateBookStatus(existingLoan.getBookId(), BookStatusEnum.LIVRE);

        bookService.updateBookStatus(updatedLoan.getBookId(), BookStatusEnum.EMPRESTADO);

        for (Loan loan : loans) {
            if (loan.getId() == id) {
                loan.setDateLoan(updatedLoan.getDateLoan());
                loan.setReturnDate(updatedLoan.getReturnDate());
                loan.setStatus(updatedLoan.getStatus());
                loan.setBookId(updatedLoan.getBookId());
                updated = true;
                break;
            }
        }

        if (!updated) throw new RuntimeException("Erro ao atualizar empréstimo.");

        try (CSVWriter writer = new CSVWriter(new FileWriter(LOAN_FILE_PATH, false))) {
            writer.writeNext(new String[]{"ID", "DateLoan", "ReturnDate", "Status", "BookID"});
            for (Loan loan : loans) {
                writer.writeNext(new String[]{
                        String.valueOf(loan.getId()),
                        sdf.format(loan.getDateLoan()),
                        sdf.format(loan.getReturnDate()),
                        loan.getStatus().name(),
                        String.valueOf(loan.getBookId())
                });
            }
        }

        return ResponseEntity.ok(updatedLoan);
    }

    public ResponseEntity<Loan> delete(@PathVariable int id) throws IOException {
        List<Loan> loans = index();
        Loan loanToDelete = loans.stream().filter(l -> l.getId() == id).findFirst().orElse(null);

        if (loanToDelete == null) throw new RuntimeException("Empréstimo não encontrado.");

        bookService.updateBookStatus(loanToDelete.getBookId(), BookStatusEnum.LIVRE);

        loans.removeIf(l -> l.getId() == id);

        try (CSVWriter writer = new CSVWriter(new FileWriter(LOAN_FILE_PATH, false))) {
            writer.writeNext(new String[]{"ID", "DateLoan", "ReturnDate", "Status", "BookID"});
            for (Loan loan : loans) {
                writer.writeNext(new String[]{
                        String.valueOf(loan.getId()),
                        sdf.format(loan.getDateLoan()),
                        sdf.format(loan.getReturnDate()),
                        loan.getStatus().name(),
                        String.valueOf(loan.getBookId())
                });
            }
        }

        return ResponseEntity.ok().build();
    }
}
