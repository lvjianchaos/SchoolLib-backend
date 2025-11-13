package com.chaos.schoollib.controller;

import com.chaos.schoollib.dto.BorrowRequestDTO;
import com.chaos.schoollib.dto.ReturnRequestDTO;
import com.chaos.schoollib.entity.BorrowRecord;
import com.chaos.schoollib.entity.User;
import com.chaos.schoollib.service.BorrowService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 借阅 API 控制器
 */
@RestController
@RequestMapping("/api")
public class BorrowController {

    private final BorrowService borrowService;

    @Autowired
    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    /**
     * 1. 借书
     * POST /api/borrow
     */
    @PostMapping("/borrow")
    public ResponseEntity<BorrowRecord> borrowBook(
            @Valid @RequestBody BorrowRequestDTO borrowRequest,
            @AuthenticationPrincipal User currentUser // 从 Security 上下文自动注入当前用户
    ) {
        // UserID 来自认证信息，而不是 DTO，更安全
        BorrowRecord record = borrowService.borrowBook(currentUser.getUserID(), borrowRequest.getBookId());
        return ResponseEntity.ok(record);
    }

    /**
     * 2. 还书
     * POST /api/return
     */
    @PostMapping("/return")
    public ResponseEntity<BorrowRecord> returnBook(
            @Valid @RequestBody ReturnRequestDTO returnRequest,
            @AuthenticationPrincipal User currentUser
    ) {
        BorrowRecord record = borrowService.returnBook(currentUser.getUserID(), returnRequest.getRecordId());
        return ResponseEntity.ok(record);
    }

    /**
     * 3. 获取我的借阅记录
     * GET /api/me/records
     */
    @GetMapping("/me/records")
    public ResponseEntity<List<BorrowRecord>> getMyRecords(
            @AuthenticationPrincipal User currentUser
    ) {
        List<BorrowRecord> records = borrowService.getMyRecords(currentUser.getUserID());
        return ResponseEntity.ok(records);
    }
}