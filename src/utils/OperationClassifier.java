package utils;

import dtos.RemoteOperation;

public class OperationClassifier {
  public static boolean isForBankService(RemoteOperation operation) {
    return !isForAuthenticationService(operation);
  }
  
  public static boolean isForAuthenticationService(RemoteOperation operation) {
    boolean isCreateAccount = RemoteOperation.CREATE_ACCOUNT.equals(operation);
    boolean isAuthentication = RemoteOperation.AUTHENTICATION.equals(operation);

    return isCreateAccount || isAuthentication;
  }
}
