package faang.school.postservice.exception;

public enum ExceptionMessage {
    FILE_EXCEPTION("error processing file"),
    EXCEPTION_MESSAGE("an error occurred, let's go have a beer");

    private String message;

    ExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
