package cz.cuni.mff.vopalenf.frontend.annotation;

import cz.cuni.mff.vopalenf.filesystemmanager.service.FileSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller for proper functioning of annotator function.
 */
@Controller
@RequestMapping("/annotator")
public class AnnotationScreenController {

    private final FileSystemService fileSystemService;

    @Autowired
    public AnnotationScreenController(final FileSystemService fileSystemService) {
        this.fileSystemService = fileSystemService;
    }

    /**
     * Takes care of setting parameters for properly generating annotation screen.
     *
     * @param id    ID of project to be annotated
     * @param model Holder for attributes
     * @return Redirection to template
     */
    @GetMapping("/{id}/page/{page}")
    public String getImagesForPage(@PathVariable Long id, @PathVariable int page, Model model) {
        int imagesOnPage = 684;
        int totalImages = fileSystemService.getFramesCount(id);
        int totalPages = (int) Math.ceil((double) totalImages / imagesOnPage);

        if (page < 1 || page > totalPages) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid page number");
        }

        model.addAttribute("id", id);
        model.addAttribute("currentPage", page);
        model.addAttribute("imagesOnPage", imagesOnPage);
        model.addAttribute("totalPages", totalPages);

        return "annotator";
    }
}
