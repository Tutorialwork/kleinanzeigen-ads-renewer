package dev.manuelschuler.kleinanzeigenadsrenewer.helper;

import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import lombok.SneakyThrows;

public class MoveMailHelper {

    private MoveMailHelper() {
    }

    @SneakyThrows
    public static void moveMail(Message mail, Folder sourceFolder, Folder destinationFolder) {
        destinationFolder.appendMessages(new Message[]{mail});
        mail.setFlag(Flags.Flag.DELETED, true);
        sourceFolder.expunge();
    }

}
