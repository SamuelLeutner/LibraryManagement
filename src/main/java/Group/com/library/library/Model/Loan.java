package Group.com.library.library.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import Group.com.library.library.Enum.LoanStatusEnum;

import java.util.Date;

public class Loan {
    private final int id;
    private String bookTitle;
    private String readerName;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date dateLoan;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date returnDate;
    private LoanStatusEnum status;

    public Loan(int id, String bookTitle, String userName, Date dateLoan, Date returnDate, LoanStatusEnum status) {
        this.id = id;
        this.bookTitle = bookTitle;
        this.readerName = userName;
        this.dateLoan = dateLoan;
        this.returnDate = returnDate;
        this.status = status;
    }

    public String getReaderName() {
        return readerName;
    }

    public void setReaderName(String readerName) {
        this.readerName = readerName;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
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
