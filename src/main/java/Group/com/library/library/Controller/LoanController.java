package Group.com.library.library.Controller;

import Group.com.library.library.Exception.LivroIndisponivelException;
import Group.com.library.library.Model.Loan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import Group.com.library.library.Service.LoanService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/loan")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    public List<Loan> index() throws IOException {
        return loanService.index();
    }

    @PostMapping
    public Loan create(@RequestBody Loan loan) throws IOException, LivroIndisponivelException {
        return loanService.create(loan);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Loan> update(@PathVariable int id, @RequestBody Loan updateLoan) throws IOException {
        return loanService.update(id, updateLoan);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Loan> delete(@PathVariable int id) throws IOException {
        return loanService.delete(id);
    }
}
