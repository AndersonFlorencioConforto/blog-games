package br.com.andersondev.infrastructure.moderation;

import br.com.andersondev.application.moderation.list.ListReportsCommand;
import br.com.andersondev.application.moderation.resolve.ResolveReportCommand;
import br.com.andersondev.infrastructure.api.PageResponse;
import br.com.andersondev.infrastructure.moderation.models.ReportResponse;
import br.com.andersondev.infrastructure.moderation.models.ResolveReportRequest;
import br.com.andersondev.infrastructure.moderation.models.ResolveReportResponse;
import br.com.andersondev.infrastructure.security.AuthenticatedUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller de administracao de denuncias (api-catalog secao 2.8).
 * Enxuto: mapeia HTTP -> command e delega para a fachada transacional.
 * Acesso restrito a ADMIN (matchers em SecurityConfig).
 */
@RestController
@RequestMapping("/api/v1/admin/reports")
public class AdminReportController {

    private final ModerationTransactionalFacade moderationFacade;

    public AdminReportController(final ModerationTransactionalFacade moderationFacade) {
        this.moderationFacade = moderationFacade;
    }

    @GetMapping
    public ResponseEntity<PageResponse<ReportResponse>> listReports(
            @RequestParam(name = "status", required = false) final String status,
            @RequestParam(name = "page", defaultValue = "0") final int page,
            @RequestParam(name = "size", defaultValue = "20") final int size
    ) {
        final var pagination = this.moderationFacade.listReports(
                ListReportsCommand.with(status, page, size));
        return ResponseEntity.ok(PageResponse.from(pagination, ReportResponse::from));
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<ResolveReportResponse> resolveReport(
            @PathVariable("id") final String id,
            @RequestBody final ResolveReportRequest request,
            @AuthenticationPrincipal final AuthenticatedUser principal
    ) {
        final var command = ResolveReportCommand.with(
                id,
                request.decision(),
                request.adminNote(),
                request.banDurationDays(),
                principal.userId()
        );
        final var output = this.moderationFacade.resolveReport(command);
        return ResponseEntity.ok(ResolveReportResponse.from(output));
    }
}
