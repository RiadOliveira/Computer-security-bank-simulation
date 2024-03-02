package utils;

import java.util.Scanner;

import process.client.ClientProcess;

public class ConsolePrinter {
  private static final String CLEAR_CONSOLE = "\033[H\033[2J";
  private static final String MOVE_TO_PREVIOUS_LINE = "\033[1A";
  private static final String CLEAR_CURRENT_LINE = "\033[2K";

  private static final String CLIENT_COMMAND_PANEL =
    "Escolha um dos comandos abaixo:\n" +
    "  1. Criar conta\n" + "  2. Autenticar-se\n" +
    "  3. Obter dados da conta\n" + "  4. Sacar\n" +
    "  5. Depositar\n" + "  6. Transferência bancária\n" +
    "  7. Obter saldo\n" + "  8. Obter projeções da poupança\n" +
    "  9. Obter projeções do investimento fixo\n" +
    "  10. Atualizar investimento fixo\n" +
    "  11. Limpar console\n";

  private static final String PRESS_ENTER_TO_CONTINUE_MESSAGE = 
    "Pressione Enter para continuar...";
  private static final String OVERWRITE_PRESS_ENTER = 
    MOVE_TO_PREVIOUS_LINE + CLEAR_CURRENT_LINE + MOVE_TO_PREVIOUS_LINE;

  public static synchronized void print(Object content) {
    System.out.print(content);
  }

  public static synchronized void println(Object content) {
    System.out.println(content);
  }

  public static synchronized void clearConsole() {
    print(CLEAR_CONSOLE);  
    System.out.flush();
  }

  public static synchronized void printClientCommandPanel() {
    println(CLIENT_COMMAND_PANEL);
    print("Insira o comando desejado: ");
  }

  public static synchronized String[] printInputNameAndScan(
    String[] inputNames, Scanner scanner
  ) {
    String[] inputsReceived = new String[inputNames.length];

    for(int ind=0 ; ind<inputNames.length ; ind++) {
      print(inputNames[ind] + ": ");
      inputsReceived[ind] = scanner.nextLine();
    }

    println("");
    return inputsReceived;
  }

  public static synchronized void displayAndWaitForEnterPressing(
    Scanner scanner
  ) {
    print(PRESS_ENTER_TO_CONTINUE_MESSAGE);
    ClientProcess.scanner.nextLine();
    print(OVERWRITE_PRESS_ENTER);
  }
}
