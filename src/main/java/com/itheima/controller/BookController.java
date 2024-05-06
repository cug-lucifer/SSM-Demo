package com.itheima.controller;

import com.itheima.domain.Book;
import com.itheima.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {
    @Autowired
    private BookService bookService;

    @PostMapping
    public Result save(@RequestBody Book book) {
        boolean fg = bookService.save(book);
        return new Result(fg,fg? Code.SAVE_OK:Code.SAVE_ERR);
    }

    @PutMapping
    public Result update(@RequestBody Book book) {
        boolean fg = bookService.update(book);
        return new Result(fg,fg? Code.UPDATE_OK:Code.UPDATE_ERR);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        boolean fg = bookService.delete(id);
        return new Result(fg,fg? Code.DELETE_OK:Code.DELETE_ERR);
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        Book book = bookService.getById(id);
        if (book == null){
            return new Result(Code.GET_ERR,"查询失败，请重试");
        }
        return new Result(book,Code.GET_OK);
    }

    @GetMapping
    public Result getAll() {
        List<Book> all = bookService.getAll();
        if (all == null || all.isEmpty()){
            return new Result(Code.GET_ERR,"查询失败，请重试");
        }
        return new Result(all,Code.GET_OK);
    }
}
