package Group.com.library.library.Controller;

import Group.com.library.library.Model.Book;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import Group.com.library.library.Service.BookService;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/books")
public class BookController  {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<Book> index() throws IOException {
        return bookService.index();
    }

    @PostMapping
    public Book create(@RequestBody Book book) throws IOException {
        return bookService.create(book);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> update(@PathVariable int id, @RequestBody Book updateBook) throws IOException {
        return bookService.update(id, updateBook);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Book> delete(@PathVariable int id) throws IOException {
        return bookService.delete(id);
    }
}
