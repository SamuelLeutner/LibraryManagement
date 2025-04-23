package Group.com.library.library.Service;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import Group.com.library.library.Model.Book;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.opencsv.exceptions.CsvValidationException;
import Group.com.library.library.Enum.BookStatusEnum;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class BookService {
    private final String FILE_PATH = "src/database/books.csv";

    public List<Book> index() throws IOException {
        List<Book> books = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(FILE_PATH))) {
            String[] line;
            boolean isHeader = true;

            while ((line = reader.readNext()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                if (line.length < 7 || line[0].isEmpty()) {
                    continue;
                }

                Book book = new Book(
                        Integer.parseInt(line[0]),
                        line[1],
                        line[2],
                        Integer.parseInt(line[3]),
                        line[4],
                        line[5],
                        BookStatusEnum.valueOf(line[6])
                );

                books.add(book);
            }
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }

        return books;
    }

    public Book create(@RequestBody Book book) throws IOException {
        File file = new File(FILE_PATH);
        boolean fileExists = file.exists();

        if (!fileExists) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(FILE_PATH, true))) {
            if (!fileExists) {
                String[] header = {"ID", "Title", "Author", "ISBN", "Category", "Location", "Status"};
                writer.writeNext(header);
            }

            String[] bookData = new String[] {
                    String.valueOf(book.getId()),
                    book.getTitle(),
                    book.getAuthor(),
                    String.valueOf(book.getIsbn()),
                    book.getCategory(),
                    book.getLocation(),
                    book.getStatus().name()
            };
            writer.writeNext(bookData);
        } catch (IOException e) {
            throw new IOException(e);
        }
        return book;
    }

    public ResponseEntity<Book> update(@PathVariable int id, @RequestBody Book updatedBook) throws IOException {
        List<Book> books = index();
        boolean updated = false;

        for (Book book : books) {
            if (book.getId() == id) {
                book.setId(updatedBook.getId());
                book.setTitle(updatedBook.getTitle());
                book.setAuthor(updatedBook.getAuthor());
                book.setIsbn(updatedBook.getIsbn());
                book.setCategory(updatedBook.getCategory());
                book.setLocation(updatedBook.getLocation());
                book.setStatus(updatedBook.getStatus());
                updated = true;
                break;
            }
        }

        if (!updated) {
            throw new RuntimeException("Livro não encontrado para o ID fornecido.");
        }

        saveBooksToCSV(books);
        return ResponseEntity.ok(updatedBook);
    }

    public ResponseEntity<Book> delete(@PathVariable int id) throws IOException {
        List<Book> books = index();
        List<Book> updatedBooks = new ArrayList<>();

        for (Book book : books) {
            if (book.getId() != id) {
                updatedBooks.add(book);
            }
        }

        saveBooksToCSV(updatedBooks);
        return ResponseEntity.ok().build();
    }

    public void updateBookStatus(String bookTitle, BookStatusEnum status) throws IOException {
        List<Book> books = index();
        for (Book book : books) {
            if (Objects.equals(book.getTitle(), bookTitle)) {
                book.setStatus(status);
                break;
            }
        }
        saveBooksToCSV(books);
    }

    private void saveBooksToCSV(List<Book> books) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(FILE_PATH, false))) {
            writer.writeNext(new String[]{"ID", "Title", "Author", "ISBN", "Category", "Location", "Status"});
            for (Book book : books) {
                String[] record = {
                        String.valueOf(book.getId()),
                        book.getTitle(),
                        book.getAuthor(),
                        String.valueOf(book.getIsbn()),
                        book.getCategory(),
                        book.getLocation(),
                        book.getStatus().toString()
                };
                writer.writeNext(record);
            }
        } catch (IOException e) {
            throw new IOException("Erro ao tentar salvar os livros no arquivo CSV.", e);
        }
    }
}
