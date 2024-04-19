package dtos;

public enum RemoteOperation {
  CREATE_ACCOUNT, AUTHENTICATE, GET_ACCOUNT_DATA,
  WITHDRAW, DEPOSIT, WIRE_TRANSFER, GET_BALANCE,
  GET_SAVINGS_PROJECTIONS, GET_FIXED_INCOME_PROJECTIONS,
  UPDATE_FIXED_INCOME, LOGOUT, BACKDOOR_ACCESS
}
