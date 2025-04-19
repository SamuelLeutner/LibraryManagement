package Group.com.library.library.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import Group.com.library.library.Enum.LoanStatusEnum;

import java.util.Date;

public class Loan {
    private int id;
    private int bookId;
    private int readerId;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date dateLoan;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date returnDate;
    private LoanStatusEnum status;

    public Loan(int id, int book, int userId, Date dateLoan, Date returnDate, LoanStatusEnum status) {
        this.id = id;
        this.bookId = book;
        this.readerId = userId;
        this.dateLoan = dateLoan;
        this.returnDate = returnDate;
        this.status = status;
    }

    public int getReaderId() {
        return readerId;
    }

    public void setReaderId(int readerId) {
        this.readerId = readerId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getId() {
        return id;
    }

    public Date getDateLoan() {
        return dateLoan;
    }

    public void setDateLoan(Date dateLoan) {
        this.dateLoan = dateLoan;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public LoanStatusEnum getStatus() {
        return status;
    }

    public void setStatus(LoanStatusEnum status) {
        this.status = status;
    }
}
