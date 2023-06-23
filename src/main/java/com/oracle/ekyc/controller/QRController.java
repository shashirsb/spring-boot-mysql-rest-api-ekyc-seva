package com.oracle.ekyc.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.oracle.ekyc.utils.Utils;

@RestController
@RequestMapping("/qr")
public class QRController {

    @CrossOrigin(origins = "*", methods = { RequestMethod.GET })
    @GetMapping("/refresh")
    public String getNewQR() {

        Utils utils = new Utils();
        return "SEVA" + utils.getRandomAlphabets() + utils.getNanoTime();
    }
}
