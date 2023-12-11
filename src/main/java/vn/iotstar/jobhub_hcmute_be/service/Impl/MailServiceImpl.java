package vn.iotstar.jobhub_hcmute_be.service.Impl;

import com.lowagie.text.DocumentException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import vn.iotstar.jobhub_hcmute_be.dto.SendInvoiceRequest;
import vn.iotstar.jobhub_hcmute_be.entity.Transactions;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.repository.TransactionsRepository;
import vn.iotstar.jobhub_hcmute_be.service.MailService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
public class MailServiceImpl implements MailService {
    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    TransactionsRepository transactionsService;

    @Override
    @Async
    public ActionResult sendInvoice(SendInvoiceRequest request) {
        ActionResult actionResult = new ActionResult();

        // Convert HTML to PDF
        try {
            Optional<Transactions> transactions = transactionsService.findById(request.getTransactionId());
            if (transactions.isEmpty()) {
                actionResult.setErrorCode(ErrorCodeEnum.NOT_FOUND);
                return actionResult;
            }
            Transactions transaction = transactions.get();

            Context context = new Context();
            context.setLocale(new Locale("vi", "VN"));

            context.setVariables(
                    Map.of(
                            "transactionId", transaction.getId(),
                            "transactionDate", transaction.getCreateAt(),
                            "companyName", transaction.getEmployer().getCompanyName(),
                            "companyAddress", transaction.getEmployer().getAddress(),
                            "code", transaction.getCode(),
                            "nameCode", transaction.getName(),
                            "amount", transaction.getAmount()
                    ));


            // Load Thymeleaf template
            String htmlContent = templateEngine.process("invoice-template", context);


            byte[] pdfBytes = convertHtmlToPdf(htmlContent);

            // Attach PDF to the email
            sendEmailWithAttachment(transaction.getEmployer().getEmail(), "Invoice", "Please find the invoice attached.", pdfBytes);

            actionResult.setErrorCode(ErrorCodeEnum.SEND_INVOICE_SUCCESS);
        } catch (IOException | MessagingException e) {
            e.printStackTrace();
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }

        return actionResult;
    }

    private byte[] convertHtmlToPdf(String htmlContent) throws IOException {
        File tempFile = File.createTempFile("invoice", ".pdf");

        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(fos);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return Files.readAllBytes(tempFile.toPath());
    }

    private void sendEmailWithAttachment(String to, String subject, String text, byte[] attachment) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);

        // Attach PDF
        InputStreamSource source = new ByteArrayResource(attachment);
        helper.addAttachment("invoice.pdf", source);

        emailSender.send(message);
    }
}
