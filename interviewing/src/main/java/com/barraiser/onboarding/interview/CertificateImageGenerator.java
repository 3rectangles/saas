package com.barraiser.onboarding.interview;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import com.barraiser.onboarding.files.FileManagementService;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Log4j2
@AllArgsConstructor
@Component
public class CertificateImageGenerator {

    private FileManagementService fileManagementService;

    public String createAndUploadToS3(String candidateName, String domainName, Long issueDate, String certificateId ) throws Exception {
        Date date = new Date(issueDate*1000);
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        String formattedDate = format.format(date);
        final BufferedImage image = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("BR_certificate.jpg"));
        final Font font = new Font("Arial", Font.ROMAN_BASELINE, 58);

        final Graphics g = image.getGraphics();
        g.setFont(font);
        g.setColor(Color.BLACK);
        final FontMetrics metrics = g.getFontMetrics(font);
        final int positionX = (image.getWidth() - metrics.stringWidth(candidateName)) / 2;
        final int positionY = (image.getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
        final int positionX1 = (image.getWidth() - metrics.stringWidth(domainName)) / 2;
        final int positionY1 = (image.getHeight() +(11/2)* metrics.getHeight()+10) / 2 + metrics.getAscent();
        final int positionX2 = (image.getWidth() + metrics.stringWidth(formattedDate)-160) / 2;
        final int positionY2 = (image.getHeight() +(12)* metrics.getHeight() +10) / 2 + metrics.getAscent();

        g.drawString(candidateName, positionX, positionY);
        g.drawString(domainName, positionX1, positionY1);
        final Font font1 = new Font("Arial", Font.PLAIN, 23);

        final Graphics g1 = image.getGraphics();
        g1.setFont(font1);
        g1.setColor(Color.BLACK);
        g1.drawString(formattedDate, positionX2, positionY2);
        Path tempDirectory = Files.createTempDirectory("certificates-" + certificateId);
        Path tempFile = Files.createTempFile(tempDirectory, "CertificateImage-"+ certificateId, ".png");
        ImageIO.write(image,"png",tempFile.toFile() );

        String imageUrl = this.fileManagementService.saveImage(tempFile.toFile(), certificateId);
        tempFile.toFile().delete();
        tempDirectory.toFile().delete();
        return imageUrl;
    }

}
