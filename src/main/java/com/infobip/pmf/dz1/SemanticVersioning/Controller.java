package com.infobip.pmf.dz1.SemanticVersioning;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/versions")
public class Controller {
    SemanticVersioning semanticVersioning;

    @Autowired
    public Controller(SemanticVersioning semanticVersioning) {
        this.semanticVersioning = semanticVersioning;
    }

    @GetMapping(path = "/max")
    public String maxVersion(@RequestParam String v1, @RequestParam String v2) {
        return semanticVersioning.getMaxSemanticVersion(v1, v2);
    }

    @GetMapping(path = "/next")
    public String nextVersion(@RequestParam String v, @RequestParam String type) {
        return semanticVersioning.getNextSemanticVersion(v, type);
    }

}
