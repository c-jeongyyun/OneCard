package _oneCard;
import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class GameManager {

    // 변수 선언
    /* 사용자 번호 및 차례 관련 변수 */
    private int loginPlNum; // 로그인한 player 번호
    private int totalPlNum; // 총 게임 참여인원
    private int curTurnPlNum; // 현재 차례 player 번호
    private int pastTurnPlNum; // 이전 차례 player 번호
    private int turnKeyJ=0;
    private int turnKeyQ=0;
    private int nextTurn=0; // 다음 차례 ---------계산하는 메소드 필요 // 임시 값
    /* 파산 관련 변수 */
    private int rank; // 파산 시 출력하는 등수 (파산한 사람 있을 경우 꼴등부터 1씩 감소)
    private int bankruptCNum; // 파산 카드 개수


    /* Rule 객체 */
    private Rule rule; // rule 객체
    private ArrayList<Player> players; // 플레이어 객체 배열
    private ArrayList<Card> grave=new ArrayList<Card>(0); // 묘지
    private ArrayList<Card> stack=new ArrayList<Card>(0); // 스택

    /* 현재 게임 상황 관련 변수 */
    private ArrayList<Integer> option; // 옵션 설정 현황
    private ArrayList<int[]> attackArr; // 공격한 player 및 내용 저장 -- 공격 상황 여부 판단할 때는 배열 크기 == 0 인지 확인하면 된다.


    /* 파일 관련 변수 */
    private final String FILE_NAME="GameData.txt";
    private FileReader fReader = null;
    private BufferedReader bReader = null;
    private Scanner sc=new Scanner(System.in);


    // 생성자
    /* 생성자 */
    public GameManager (int loginPlNum, int totalPlNum, ArrayList<Integer> option) {

        // 변수 초기화 - 로그인한 사용자 번호 / 전체 사용자 수 / 옵션 설정 내역
        this.loginPlNum = loginPlNum;
        this.totalPlNum = totalPlNum;
        this.option=option;
        this.pastTurnPlNum = this.readTurn()[0];
        this.curTurnPlNum = this.readTurn()[1];
        this.turnKeyJ = this.readTurn()[2];
        this.turnKeyQ = this.readTurn()[3];

        /* 파산 변수 초기화 */
        this.bankruptCNum = setBankruptCNum(totalPlNum);
        this.rank=totalPlNum;


        // Player 배열 초기화
        this.players=new ArrayList<Player>(totalPlNum);

        // 파일 내용 읽어서 Player 객체 생성
        for (int i=0;i<this.totalPlNum;i++) {
            Player player = new Player(i);
            players.add(i, player);
        }

        // Rule객체 생성
        this.rule=new Rule();

        // 공격상황 초기화
        readAttack();
        // grave 초기화
        readAddGrave();
        // stack 초기화
        readAddStack();

    }




    // 화면 보여주는 함수
    /* 공격 상황, 일반 상황에 따라 패 보여주는 메소드 - 정윤 */
    private void showProperWindow() {
        if(isAttacked())
            showAttackCards();
        else  /// 화면 보이기 --------------제츨 후 수줭
        	showCards();
    }

    //민정 - 패턴출력
    private void printPattern(Card card) {
        String pattern = "";

        if(card.pattern()==0) {
            pattern = "♥";
        }else if(card.pattern()==1) {
            pattern = "◆";
        }else if(card.pattern()==2) {
            pattern = "♠";
        }else if(card.pattern()==3) {
            pattern = "♣";
        }else { //조커
            if(card.number()==0) {//컬러조커
                pattern = "C";
            }else {
                pattern = "B";
            }
        }

        System.out.print(pattern);
    }

    //민정 - 숫자부분출력
    private void printNumber(Card card) {
        // 숫자 확인
        String number = "";

        if (card.number() == 0) { // K
            if (card.pattern() != 4) {
                number = "K";
            } else { // 조커라면
                number = "JOKER";
            }
        } else if (card.number() == 1) {
            if(card.pattern()!=4) {
                number = "A";
            }else {
                number = "JOKER";
            }

        } else if ((2 <= card.number()) && (card.number() <= 10)) {
            number = String.valueOf(card.number());
        } else if (card.number() == 11) {
            number = "J";
        } else { // 12 : Q
            number = "Q";
        }

        System.out.print(number);

    }

    // 민정 - 추가
    public void printNormalCard(Card card) {
        
        System.out.print("|   ");
        printPattern(card);
        System.out.print(" ");
        printNumber(card);
        System.out.print("   |");
        System.out.print("\t");
    }

    // 민정 - 추가
    public void printJokerCard(Card card) {
        
        System.out.print("| ");
        printPattern(card);
        System.out.print("_");
        printNumber(card);
        System.out.print("|");
        System.out.print("\t");

    }

    // 민정 - 추가
    public void printCard(Card card) {
        if (card.pattern() != 4) {
            printNormalCard(card);
        } else {
            printJokerCard(card);
        }
    }

    // gameManager 메인 함수
    /* 사용자 패 보여주는 함수 */
    private void showCards() { // ---- 구현 필요
       // String pattern=""; String number=""; /*int number2=0;*/
       Card card = this.grave.get(grave.size() - 1);
       System.out.println("------------------------ 턴 : 사용자 " + (curTurnPlNum +1)+ "------------------------\n");
       // loginPlNum에 해당하는 번호 가진
       if (pastTurnPlNum != -1) {

          // 묘지의 마지막 인덱스 == 바로 전 사용자가 낸 카드
          // 문양 확인
          
          if(curTurnPlNum==3) {
             pastTurnPlNum=0;
          }

          System.out.print(
                "사용자 " + (pastTurnPlNum+1) + "(" + this.players.get(pastTurnPlNum).getEmail() + ")" + " 이(가) " + "[ ");
          // if(card.pattern())
          if (card.pattern() != 4) {
             printPattern(card);
             System.out.print(" ");
             printNumber(card);
          } else {
             printPattern(card);
             System.out.print("_");
             printNumber(card);
          }

          System.out.print(" ] 을 마지막으로 제시하였습니다.\n");
          System.out.println();
       } else {
          System.out.println(); // 이전 사용자 없으면 아무것도 출력 x
       }

       System.out.println("<남은 카드 수>\n");
       System.out.print("[");
       for (int i = 0; i < this.players.size(); i++) {

          if (i == (this.players.size() - 1)) {
             System.out.print("사용자 " + (i + 1) + " : " + this.players.get(i).pCard.size());
          } else {
             System.out.print("사용자 " + (i + 1) + " : " + this.players.get(i).pCard.size() + " / ");
          }
       }
       System.out.print("]\n");

       if (card.pattern() != 4) { // 조커가 아니라면
          System.out.println(" -------------------");
          for (int i = 0; i < 4; i++) {
             System.out.println("|                   |");
          }

          System.out.print("|         ");
          printPattern(card);
          System.out.print("         |\n");

          System.out.println("|                   |");
          System.out.print("|         ");
          printNumber(card);
          System.out.print("         |\n");
          for (int i = 0; i < 4; i++) {
             System.out.println("|                   |");
          }
          System.out.println(" -------------------");
          System.out.println();
          System.out.println();
       } else {// 조커라면
          System.out.println(" -------------------");
          for (int i = 0; i < 4; i++) {
             System.out.println("|                   |");
          }

          System.out.print("|         ");
          printPattern(card);
          System.out.print("         |\n");

          System.out.println("|                   |");
          System.out.print("|       ");
          printNumber(card);
          System.out.print("       |\n");
          for (int i = 0; i < 4; i++) {
             System.out.println("|                   |");
          }
          System.out.println(" -------------------");
       }
       System.out.println();
       System.out.println();

       // 사용자 패 출력
       int size = this.players.get(loginPlNum).pCard.size();
       if (size > 4) {
          if (size > 8) {// 9장 이상

             if (size > 16) { // 17장 이상 ==> 4줄
                // 첫줄
                for (int k = 0; k < 4; k++) {
                   System.out.print("-----------");
                   System.out.print("     ");
                }
                System.out.println();
                for (int s = 0; s < 4; s++) {
                   Card curTurnCard = this.players.get(loginPlNum).pCard.get(s);
                   printCard(curTurnCard);
                }
                System.out.println();
                // 둘째줄
                for (int t = 4; t < 8; t++) {
                   System.out.print("-----------");
                   System.out.print("     ");
                }
                System.out.println();
                for (int u = 4; u < 8; u++) {
                   Card curTurnCard = this.players.get(loginPlNum).pCard.get(u);
                   printCard(curTurnCard);
                }
                System.out.println();
                // 셋째줄
                for (int v = 8; v < 16; v++) {
                   System.out.print("-----------");
                   System.out.print("     ");
                }
                System.out.println();
                for (int y = 8; y < 16; y++) {
                   Card curTurnCard = this.players.get(loginPlNum).pCard.get(y);
                   printCard(curTurnCard);
                }
                System.out.println();
                // 넷째줄
                for (int p = 16; p < size; p++) {
                   System.out.print("-----------");
                   System.out.print("     ");
                }
                System.out.println();
                for (int q = 16; q < size; q++) {
                   Card curTurnCard = this.players.get(loginPlNum).pCard.get(q);
                   printCard(curTurnCard);
                }
                System.out.println();
             } else {// 9장 이상 16장 이하 ==> 3줄
                   // 첫줄
                for (int k = 0; k < 4; k++) {
                   System.out.print("-----------");
                   System.out.print("     ");
                }
                System.out.println();
                for (int s = 0; s < 4; s++) {
                   Card curTurnCard = this.players.get(loginPlNum).pCard.get(s);
                   printCard(curTurnCard);
                }
                System.out.println();
                // 둘째줄
                for (int t = 4; t < 8; t++) {
                   System.out.print("-----------");
                   System.out.print("     ");
                }
                System.out.println();
                for (int u = 4; u < 8; u++) {
                   Card curTurnCard = this.players.get(loginPlNum).pCard.get(u);
                   printCard(curTurnCard);
                }
                System.out.println();
                // 셋째줄
                for (int v = 8; v < size; v++) {
                   System.out.print("-----------");
                   System.out.print("     ");
                }
                System.out.println();
                for (int y = 8; y < size; y++) {
                   Card curTurnCard = this.players.get(loginPlNum).pCard.get(y);
                   printCard(curTurnCard);
                }
                System.out.println();
             }

          } else {// 5장 이상 8장 이하 ==>2줄
                // 첫 줄 4장
             for (int k = 0; k < 4; k++) {
                System.out.print("-----------");
                System.out.print("     ");
             }
             System.out.println();
             for (int s = 0; s < 4; s++) {
                Card curTurnCard = this.players.get(loginPlNum).pCard.get(s);
                printCard(curTurnCard);
             }
             System.out.println();
             // 다음줄 나머지
             for (int t = 4; t < size; t++) {
                System.out.print("-----------");
                System.out.print("     ");
             }
             System.out.println();
             for (int u = 4; u < size; u++) {
                Card curTurnCard = this.players.get(loginPlNum).pCard.get(u);
                printCard(curTurnCard);
             }
             System.out.println();

          }
       } else { // 카드가 4장 이하 ==>한줄
          for (int k = 0; k < size; k++) {
             System.out.print("-----------");
             System.out.print("     ");
          }
          System.out.println();
          for (int s = 0; s < size; s++) {
             Card curTurnCard = this.players.get(loginPlNum).pCard.get(s);
             printCard(curTurnCard);
          }
          System.out.println();

       }
       System.out.println();

    }


    // 민정 /* 공격 상황 시 패 보여주는 함수 */
    private void showAttackCards() {
       Card precard = this.grave.get(grave.size() - 1);
       System.out.println("------------------------ 턴 : 사용자 " + (curTurnPlNum+1) + "------------------------\n");
       // loginPlNum에 해당하는 번호 가진
       if (pastTurnPlNum != -1) {

          // 묘지의 마지막 인덱스 == 바로 전 사용자가 낸 카드
          // 문양 확인

          System.out.print(
                "사용자 " + pastTurnPlNum + "(" + this.players.get(pastTurnPlNum).getEmail() + ")" + " 이(가) " + "[ ");
          // if(card.pattern())
          if (precard.pattern() != 4) {
             printPattern(precard);
             System.out.print(" ");
             printNumber(precard);
          } else {
             printPattern(precard);
             System.out.print("_");
             printNumber(precard);
          }

          System.out.print(" ] 을 마지막으로 제시하였습니다.\n");
          System.out.println();
       } else {
          System.out.println(); // 이전 사용자 없으면 아무것도 출력 x
       }

       System.out.println("<남은 카드 수>\n");
       System.out.print("[");
       for (int i = 0; i < this.players.size(); i++) {

          if (i == (this.players.size() - 1)) {
             System.out.print("사용자 " + (i + 1) + " : " + this.players.get(i).pCard.size());
          } else {
             System.out.print("사용자 " + (i + 1) + " : " + this.players.get(i).pCard.size() + " / ");
          }
       }
       System.out.print("]\n");

       if (precard.pattern() != 4) { // 조커가 아니라면
          System.out.println(" -------------------");
          for (int i = 0; i < 4; i++) {
             System.out.println("|                   |");
          }

          System.out.print("|         ");
          printPattern(precard);
          System.out.print("         |\n");

          System.out.println("|                   |");
          System.out.print("|         ");
          printNumber(precard);
          System.out.print("         |\n");
          for (int i = 0; i < 4; i++) {
             System.out.println("|                   |");
          }
          System.out.println(" -------------------");
          System.out.println();
          System.out.println();
       } else {// 조커라면
          System.out.println(" -------------------");
          for (int i = 0; i < 4; i++) {
             System.out.println("|                   |");
          }

          System.out.print("|         ");
          printPattern(precard);
          System.out.print("         |\n");

          System.out.println("|                   |");
          System.out.print("|       ");
          printNumber(precard);
          System.out.print("       |\n");
          for (int i = 0; i < 4; i++) {
             System.out.println("|                   |");
          }
          System.out.println(" -------------------");
       }
       System.out.println();
       System.out.println();

       // 누적된 공격상황 보여주기
       for (int i = 0; i < this.attackArr.size(); i++) {
          int[] innerArr = new int[4];
          innerArr = this.attackArr.get(i);
          Card attackCard = new Card(innerArr[2], innerArr[3]);
          // Card attackCard = this.players.get(innerArr[0]-1).pCard.get();
          System.out.print("사용자 " + innerArr[0] + "의 마지막 카드 [");
          if (attackCard.pattern() != 4) {
             printPattern(attackCard);
             System.out.print(" ");
             printNumber(attackCard);
          } else {
             printPattern(attackCard);
             System.out.print("_");
             printNumber(attackCard);
          }

          System.out.print(" ] : +" + innerArr[1] + "장\n");
       }
       System.out.println("공격카드로 현재 카드가 " + this.getPenaltyCards() + "장 누적되어 있습니다.\n\n");

       // 현재 사용자 보유패 출력
       // 사용자 패 출력
       // 사용자 패 출력
             int size = this.players.get(loginPlNum).pCard.size();
             if (size > 4) {
                if (size > 8) {// 9장 이상

                   if (size > 16) { // 17장 이상 ==> 4줄
                      // 첫줄
                      for (int k = 0; k < 4; k++) {
                         System.out.print("-----------");
                         System.out.print("     ");
                      }
                      System.out.println();
                      for (int s = 0; s < 4; s++) {
                         Card curTurnCard = this.players.get(loginPlNum).pCard.get(s);
                         printCard(curTurnCard);
                      }
                      System.out.println();
                      // 둘째줄
                      for (int t = 4; t < 8; t++) {
                         System.out.print("-----------");
                         System.out.print("     ");
                      }
                      System.out.println();
                      for (int u = 4; u < 8; u++) {
                         Card curTurnCard = this.players.get(loginPlNum).pCard.get(u);
                         printCard(curTurnCard);
                      }
                      System.out.println();
                      // 셋째줄
                      for (int v = 8; v < 16; v++) {
                         System.out.print("-----------");
                         System.out.print("     ");
                      }
                      System.out.println();
                      for (int y = 8; y < 16; y++) {
                         Card curTurnCard = this.players.get(loginPlNum).pCard.get(y);
                         printCard(curTurnCard);
                      }
                      System.out.println();
                      // 넷째줄
                      for (int p = 16; p < size; p++) {
                         System.out.print("-----------");
                         System.out.print("     ");
                      }
                      System.out.println();
                      for (int q = 16; q < size; q++) {
                         Card curTurnCard = this.players.get(loginPlNum).pCard.get(q);
                         printCard(curTurnCard);
                      }
                      System.out.println();
                   } else {// 9장 이상 16장 이하 ==> 3줄
                         // 첫줄
                      for (int k = 0; k < 4; k++) {
                         System.out.print("-----------");
                         System.out.print("     ");
                      }
                      System.out.println();
                      for (int s = 0; s < 4; s++) {
                         Card curTurnCard = this.players.get(loginPlNum).pCard.get(s);
                         printCard(curTurnCard);
                      }
                      System.out.println();
                      // 둘째줄
                      for (int t = 4; t < 8; t++) {
                         System.out.print("-----------");
                         System.out.print("     ");
                      }
                      System.out.println();
                      for (int u = 4; u < 8; u++) {
                         Card curTurnCard = this.players.get(loginPlNum).pCard.get(u);
                         printCard(curTurnCard);
                      }
                      System.out.println();
                      // 셋째줄
                      for (int v = 8; v < size; v++) {
                         System.out.print("-----------");
                         System.out.print("     ");
                      }
                      System.out.println();
                      for (int y = 8; y < size; y++) {
                         Card curTurnCard = this.players.get(loginPlNum).pCard.get(y);
                         printCard(curTurnCard);
                      }
                      System.out.println();
                   }

                } else {// 5장 이상 8장 이하 ==>2줄
                      // 첫 줄 4장
                   for (int k = 0; k < 4; k++) {
                      System.out.print("-----------");
                      System.out.print("     ");
                   }
                   System.out.println();
                   for (int s = 0; s < 4; s++) {
                      Card curTurnCard = this.players.get(loginPlNum).pCard.get(s);
                      printCard(curTurnCard);
                   }
                   System.out.println();
                   // 다음줄 나머지
                   for (int t = 4; t < size; t++) {
                      System.out.print("-----------");
                      System.out.print("     ");
                   }
                   System.out.println();
                   for (int u = 4; u < size; u++) {
                      Card curTurnCard = this.players.get(loginPlNum).pCard.get(u);
                      printCard(curTurnCard);
                   }
                   System.out.println();

                }
             } else { // 카드가 4장 이하 ==>한줄
                for (int k = 0; k < size; k++) {
                   System.out.print("-----------");
                   System.out.print("     ");
                }
                System.out.println();
                for (int s = 0; s < size; s++) {
                   Card curTurnCard = this.players.get(loginPlNum).pCard.get(s);
                   printCard(curTurnCard);
                }
                System.out.println();

             }
             System.out.println();

    }

    // 민정 /* 방어상황*/
    private void showDefense() {
       Card precard = this.grave.get(grave.size() - 1);
       System.out.println("------------------------ 턴 : 사용자 " + (curTurnPlNum+1) + "------------------------\n");
       // loginPlNum에 해당하는 번호 가진
       if (pastTurnPlNum != -1) {

          // 묘지의 마지막 인덱스 == 바로 전 사용자가 낸 카드
          // 문양 확인

          System.out.print(
                "사용자 " + pastTurnPlNum + "(" + this.players.get(pastTurnPlNum).getEmail() + ")" + " 이(가) " + "[ ");
          // if(card.pattern())
          if (precard.pattern() != 4) {
             printPattern(precard);
             System.out.print(" ");
             printNumber(precard);
          } else {
             printPattern(precard);
             System.out.print("_");
             printNumber(precard);
          }

          System.out.print(" ] 을 마지막으로 제시하였습니다.\n");
          System.out.println();
       } else {
          System.out.println(); // 이전 사용자 없으면 아무것도 출력 x
       }

       System.out.println("<남은 카드 수>\n");
       System.out.print("[");
       for (int i = 0; i < this.players.size(); i++) {

          if (i == (this.players.size() - 1)) {
             System.out.print("사용자 " + (i + 1) + " : " + this.players.get(i).pCard.size());
          } else {
             System.out.print("사용자 " + (i + 1) + " : " + this.players.get(i).pCard.size() + " / ");
          }
       }
       System.out.print("]\n");

       if (precard.pattern() != 4) { // 조커가 아니라면
          System.out.println(" -------------------");
          for (int i = 0; i < 4; i++) {
             System.out.println("|                   |");
          }

          System.out.print("|         ");
          printPattern(precard);
          System.out.print("         |\n");

          System.out.println("|                   |");
          System.out.print("|         ");
          printNumber(precard);
          System.out.print("         |\n");
          for (int i = 0; i < 4; i++) {
             System.out.println("|                   |");
          }
          System.out.println(" -------------------");
          System.out.println();
          System.out.println();
       } else {// 조커라면
          System.out.println(" -------------------");
          for (int i = 0; i < 4; i++) {
             System.out.println("|                   |");
          }

          System.out.print("|         ");
          printPattern(precard);
          System.out.print("         |\n");

          System.out.println("|                   |");
          System.out.print("|       ");
          printNumber(precard);
          System.out.print("       |\n");
          for (int i = 0; i < 4; i++) {
             System.out.println("|                   |");
          }
          System.out.println(" -------------------");
       }
       System.out.println();
       System.out.println();

       // 누적된 공격상황 보여주기
       for (int i = 0; i < this.attackArr.size(); i++) {
          int[] innerArr = new int[4];
          innerArr = this.attackArr.get(i);
          Card attackCard = new Card(innerArr[2], innerArr[3]);
          // Card attackCard = this.players.get(innerArr[0]-1).pCard.get();
          System.out.print("사용자 " + innerArr[0] + "의 마지막 카드 [");
          if (attackCard.pattern() != 4) {
             printPattern(attackCard);
             System.out.print(" ");
             printNumber(attackCard);
          } else {
             printPattern(attackCard);
             System.out.print("_");
             printNumber(attackCard);
          }

          System.out.print(" ] : +" + innerArr[1] + "장\n");
       }

       // 직전 사용자의 방어카드 3 제시내용 출력
       System.out.print("사용자 " + this.pastTurnPlNum + "의 마지막 카드 [");

       // 어차피 직전 사용자의 카드가 3이긴 하지만 혹시 몰라서 if문으로 똑같이 구분해 두었습니당..
       if (precard.pattern() != 4) {
          printPattern(precard);
          System.out.print(" ");
          printNumber(precard);
       } else {
          printPattern(precard);
          System.out.print("_");
          printNumber(precard);
       }
       System.out.print(" ] \n");
       System.out.println("방어에 성공했습니다! 누적된 카드가 사라집니다.");
       System.out.println();

       // 현재 사용자 보유패 출력
       // 사용자 패 출력
             int size = this.players.get(loginPlNum).pCard.size();
             if (size > 4) {
                if (size > 8) {// 9장 이상

                   if (size > 16) { // 17장 이상 ==> 4줄
                      // 첫줄
                      for (int k = 0; k < 4; k++) {
                         System.out.print("-----------");
                         System.out.print("     ");
                      }
                      System.out.println();
                      for (int s = 0; s < 4; s++) {
                         Card curTurnCard = this.players.get(loginPlNum).pCard.get(s);
                         printCard(curTurnCard);
                      }
                      System.out.println();
                      // 둘째줄
                      for (int t = 4; t < 8; t++) {
                         System.out.print("-----------");
                         System.out.print("     ");
                      }
                      System.out.println();
                      for (int u = 4; u < 8; u++) {
                         Card curTurnCard = this.players.get(loginPlNum).pCard.get(u);
                         printCard(curTurnCard);
                      }
                      System.out.println();
                      // 셋째줄
                      for (int v = 8; v < 16; v++) {
                         System.out.print("-----------");
                         System.out.print("     ");
                      }
                      System.out.println();
                      for (int y = 8; y < 16; y++) {
                         Card curTurnCard = this.players.get(loginPlNum).pCard.get(y);
                         printCard(curTurnCard);
                      }
                      System.out.println();
                      // 넷째줄
                      for (int p = 16; p < size; p++) {
                         System.out.print("-----------");
                         System.out.print("     ");
                      }
                      System.out.println();
                      for (int q = 16; q < size; q++) {
                         Card curTurnCard = this.players.get(loginPlNum).pCard.get(q);
                         printCard(curTurnCard);
                      }
                      System.out.println();
                   } else {// 9장 이상 16장 이하 ==> 3줄
                         // 첫줄
                      for (int k = 0; k < 4; k++) {
                         System.out.print("-----------");
                         System.out.print("     ");
                      }
                      System.out.println();
                      for (int s = 0; s < 4; s++) {
                         Card curTurnCard = this.players.get(loginPlNum).pCard.get(s);
                         printCard(curTurnCard);
                      }
                      System.out.println();
                      // 둘째줄
                      for (int t = 4; t < 8; t++) {
                         System.out.print("-----------");
                         System.out.print("     ");
                      }
                      System.out.println();
                      for (int u = 4; u < 8; u++) {
                         Card curTurnCard = this.players.get(loginPlNum).pCard.get(u);
                         printCard(curTurnCard);
                      }
                      System.out.println();
                      // 셋째줄
                      for (int v = 8; v < size; v++) {
                         System.out.print("-----------");
                         System.out.print("     ");
                      }
                      System.out.println();
                      for (int y = 8; y < size; y++) {
                         Card curTurnCard = this.players.get(loginPlNum).pCard.get(y);
                         printCard(curTurnCard);
                      }
                      System.out.println();
                   }

                } else {// 5장 이상 8장 이하 ==>2줄
                      // 첫 줄 4장
                   for (int k = 0; k < 4; k++) {
                      System.out.print("-----------");
                      System.out.print("     ");
                   }
                   System.out.println();
                   for (int s = 0; s < 4; s++) {
                      Card curTurnCard = this.players.get(loginPlNum).pCard.get(s);
                      printCard(curTurnCard);
                   }
                   System.out.println();
                   // 다음줄 나머지
                   for (int t = 4; t < size; t++) {
                      System.out.print("-----------");
                      System.out.print("     ");
                   }
                   System.out.println();
                   for (int u = 4; u < size; u++) {
                      Card curTurnCard = this.players.get(loginPlNum).pCard.get(u);
                      printCard(curTurnCard);
                   }
                   System.out.println();

                }
             } else { // 카드가 4장 이하 ==>한줄
                for (int k = 0; k < size; k++) {
                   System.out.print("-----------");
                   System.out.print("     ");
                }
                System.out.println();
                for (int s = 0; s < size; s++) {
                   Card curTurnCard = this.players.get(loginPlNum).pCard.get(s);
                   printCard(curTurnCard);
                }
                System.out.println();

             }
             System.out.println();


    }





    // 카드 내는 경우
    /* 카드 낼 때 동작하는 함수 */
    public void playCard() throws InterruptedException {
        // 공격 상황인지
        if(this.isAttacked()) {
           
            playAttack();
            return;
        }

        // 공격상황 아닌 경우 - 일반 화면 출력
        showCards(); //------------- 태아님 구현 담당

        // special Card 5 발현 상황인지
        if(canSpecial5()) {
           
            special5();  // 특수카드 5 발현상황
        }

        Card card;
        while(true) {
            card = rule.scanner(); // End 입력하는 경우도 추가 //----------------- 경민님 scanner에 추가 >_하고 입력받는거 까지/ scanner 오류 수정
            // end 입력했으면
            if(isEnd(card)) {
                // 카드 먹기
                eatCard(players.get(curTurnPlNum));
                // 카드 먹은 후 사용자 파산인지 판단
                if(isBankrupt())  // 파산이면
                    bankruptUI(); // 사용자 패 다 묘지로 보내고 종료화면 출력

                showTerminalNoCard(); //-> end 입력하면 카드 없음 terminal 출력 - 여기서 프로그램 종료
            }

            if(isProperCard(card))
                break;
            System.out.println("--- 사용할 수 없는 카드입니다. ---");
            continue;
        }

        // 공격카드인지 판단 - 맞으면 attackArr에 넣어주기
        if(rule.isAttackCard(card))
            addAttackArr(curTurnPlNum,card);

        // 낸 카드는 묘지로
        plCardToGrave(players.get(curTurnPlNum),card);

        // 만약 사용자 패 0장이라면 - 종료 화면 출력
        if(isCardZero(curTurnPlNum))
            showTerminalNormal();

        // 특수카드인 경우 - 해당 함수 수행
        applySpecialRules(card);

        // 더 낼 카드 있으면 내게하기
        hasMoreCardToPlay();

        // 현재 화면 지우고 종료화면 출력
        clearConsole();
        showTerminalNormal();

    }
// 게임 완전히 끝나면 파일 초기화 
    public void resetFlie(){
        try {
            File file = new File("GameData.txt");
            FileWriter fWriter = new FileWriter(file);
            fWriter.write("");
            fWriter.close();
            System.exit(0);
        }catch (IOException e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


 // 한명 빼고 다 0인지 - 정윤
    public boolean isZeroExceptOne() {
    	int cnt=0;
    	for(int i=0;i<totalPlNum;i++) {
    		if(players.get(i).pCard.size()!=0)
    			cnt++;
    	}
    	if(cnt==1)
    		return true;
    	return false;
    }


    // 서브화면
    /* 서브화면 메인 함수 */ //------------- 구현 해야함
    public void subMain() throws InterruptedException {
    	clearConsole();
    	showProperWindow();
    	System.out.println("\n--- 현재 사용자의 차례가 아닙니다. ---");
    	System.out.println(">> 프로그램을 종료하려면 아무 키나 입력해주세요.");
    	System.out.print(">_");
    	String answer=sc.nextLine(); // 아무거나 입력 후 엔터 
    	System.exit(0);

    }

    /* 서브화면 출력하는 상황인지 판단 */
    public boolean isSubPrompt() {
        if(checkMyTurn()==false || isCardZero(loginPlNum))
            return true;
        return false;
    }


    /* 공격상황인 경우 */



    //
// 공격 상황인 경우
    private void playAttack() throws InterruptedException {
        showAttackCards();
        while(true) {
            // 방어할건지
            int answer=askDefense();
            switch(answer) {
                // YES
                case 1:
                    Card card=rule.scanner();
                    // 높거나 같은 공격카드 낸 경우
                    if(rule.isHigherSameAttack(grave.get(grave.size()-1),card)) {
                        plCardToGrave(players.get(curTurnPlNum),card);
                        doDefenseAttack(card); // 여기서 종료
                    }
                    if(rule.isDefence3(grave.get(grave.size()-1), card)) {
                        plCardToGrave(players.get(curTurnPlNum),card);
                        doDefense(); // 여기서 종료
                    }
                    // 높은 공격카드도, 방어카드도 아닌 경우
                    System.out.println("--- 방어에 해당하지 않습니다. ---");
                    continue;

                    // NO
                case 0:
                    noDefense();

                    // ElSE
                default:
                    continue;

            }
        }
    }


    // 공격
    /* 방어 공격 수행 및 화면 출력 */
    private void doDefenseAttack(Card card) throws InterruptedException {
        addAttackArr(curTurnPlNum, card);
        hasMoreCardToPlay();
        showTerminalNormal();
    }



    // 방어
    /* 방어카드 3 냈을 때 수행하는 메소드 */
    private void doDefense() throws InterruptedException {
        clearConsole();
        showDefense();

        // attackArr 크기 0으로 만들기
        clearAttackArr();
        System.out.println(">> 더 내실 카드가 있습니까?(Y/N)");
        if(askMoreCard()==false) // 더 낼 카드가 없는 경우
            showTerminalNormal();

        playAdditionalCard();
        hasMoreCardToPlay();
    }

    // 방어 하지 않은 경우
    /* 방어하지 않은 경우에 수행되는 메소드 */
    private void noDefense() {
        int penaltyNum=getPenaltyCards();
        System.out.println("--- "+ penaltyNum +"장의 카드를 받습니다 ---");

        // 패널티 수만큼 카드 먹기
        for(int i=0;i<penaltyNum;i++)
            eatCard(players.get(curTurnPlNum));
        // attackArr에 있는 내용 모두 지워주기
        clearAttackArr();
        
        //파일갱신
        renewFileWriting();

        // 카드 먹고 파산인 경우
        if(isBankrupt())
            bankruptUI();
        showTerminalNormal(); //---- 제출 후 수정 
        System.exit(0);
    }



    // 공격 카드 배열관련
    /* 민정 - *누적된 공격 카드 개수 반환 **/
    public int getPenaltyCards() {
        int penaltyCards = 0;

        int[] arr = new int[2];
        for(int i=0; i<this.attackArr.size(); i++) {
            arr = this.attackArr.get(i);
            penaltyCards += arr[1];

        }
        return penaltyCards;
    }

    /* 민정 - attackArr 다 지우기 */
    public void clearAttackArr() {
        this.attackArr.clear();
        // 안의 내용 null 로 초기화, 사이즈 0 이 됨
    }



    // 더 낼카드 있는 경우
    /* 더 낼 카드 총괄 메소드 - 정윤 */
    private void hasMoreCardToPlay() throws InterruptedException {
        while(true) {
            clearConsole();
            // 일반인 경우 일반화면 , attack인 경우 attack 화면
            showProperWindow();
            System.out.println(">>더 내실 카드가 있나요? (Y/N)");
            if(askMoreCard()==false) // 더 낼 카드가 없는 경우
                break;
            // yes 라고 대답한 경우 - 새 화면 보여주기
            clearConsole();
            showProperWindow();

            // 카드 1장씩 추가하는 함수
            playAdditionalCard();
        }
    }

    /* 추가카드 한 장씩 내는 메소드 - 정윤 */
    private void playAdditionalCard() {
        while(true) {
            Card card = rule.scanner();
            if(isEnd(card)) // End 입력한 경우
                showTerminalNormal(); // 종료화면 출력하면서 실행 종료

            // 카드 입력한 경우 - 적절한 카드인지 판단 (조커 아니고 숫자만 같아야함 )
            if(isProperAddiCard(card)==false) { // 적절한 카드 아닌 경우
                System.out.println("--- 사용할 수 없는 카드입니다.---");
                continue;
            }

            // 공격카드인지 판단 - 맞으면 attackArr에 넣어주기
            if(rule.isAttackCard(card))
                addAttackArr(curTurnPlNum,card);

            // 특수 카드 중 J인 경우
            if(rule.isJump(card)&&option.get(0)==1) {
                turnKeyJ++;
            }

            // 특수 카드 중 Q인 경우
            if(rule.isQueen(card)&&option.get(1)==1) {
                turnKeyQ++;
            }

            // 낸 카드는 묘지로
            plCardToGrave(players.get(curTurnPlNum),card);

            // 만약 사용자 패 0장이라면 - 종료 화면 출력
            if(isCardZero(curTurnPlNum))
                showTerminalNormal();

            break;
        }
    }


    // 파일 읽어오기
    /* 차례 파일에서 읽어오는 메소드 - 민정 */
// 파일에서 읽어오는 함수
    /* 민정 - Turn 관련 내용 읽어오는 함수 */
    private int[] readTurn() {
        int[] turnArr=new int[4];
        String line = "";
        try{
            File file = new File(FILE_NAME);
            fReader = new FileReader(file);
            bReader = new BufferedReader(fReader);

            while((line=bReader.readLine())!=null){
                if(line.contains("Turn")) {
                    while((line=bReader.readLine())!=null) {
                        String[] temp = line.split(" "); //spacebar 가 구분자
                        turnArr[0] = Integer.parseInt(temp[0]);
                        turnArr[1] = Integer.parseInt(temp[1]);
                        turnArr[2] = Integer.parseInt(temp[2]);
                        turnArr[3] = Integer.parseInt(temp[3]);
                        break;
                    }

                }
            }


        } catch (FileNotFoundException e) {
            System.err.println("파일을 열 수 없습니다.");
        } catch (IOException e) {
            System.err.println("파일 읽기 에러");
        }
        finally {
            if(fReader!=null) {
                try {
                    bReader.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return turnArr;
    }

    /* 공격 상황 파일에서 읽어오는 메소드 - 민정 */
    /* 민정 - Attack 관련 내용 읽어오는 함수 */
    /* 민정 - Attack 관련 내용 읽어오는 함수 */ // 수정 : 인덱스 늘려서 뒤에 공격카드 패턴 숫자까지 읽어오도록
    private void readAttack() {
        attackArr = new ArrayList<int[]>();
        String line = "";
        // 파일 읽기 시작
        try {
            File file = new File(FILE_NAME);
            fReader = new FileReader(file);
            bReader = new BufferedReader(fReader);

            while ((line = bReader.readLine()) != null) {

                if (line.contains("Attack")) {
                    while ((line = bReader.readLine()) != null) {

                        if (line.contains("/")) {
                            break;
                        }

                        int[] innerArr = new int[4];
                        String[] temp = line.split(" ");
                        innerArr[0] = Integer.parseInt(temp[0]);
                        innerArr[1] = Integer.parseInt(temp[1]);
                        innerArr[2] = Integer.parseInt(temp[2]);
                        innerArr[3] = Integer.parseInt(temp[3]);
                        attackArr.add(innerArr);
                    }
                }

            }

        } catch (FileNotFoundException e) {
            System.err.println("파일을 열 수 없습니다.");
        } catch (IOException e) {
            System.err.println("파일 읽기 에러");
        } finally {
            if (fReader != null) {
                try {
                    bReader.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

    /* 묘지 파일에서 읽어오는 메소드 - 민정 */
    /* 민정 - 데이터 파일의 Grave부분 읽어서 grave에 카드 넣는 메소드 */
    private void readAddGrave() {
        String line = "";
        int plPattern;
        int plNumber;
        try{
            File file = new File(FILE_NAME);
            fReader = new FileReader(file);
            bReader = new BufferedReader(fReader);

            while((line=bReader.readLine())!=null){

                if(line.contains("Grave")) {
                    while((line=bReader.readLine())!=null) {

                        if(line.contains("/")) {
                            break;
                        }
                        String[] temp = line.split(" ");
                        plPattern = Integer.parseInt(temp[0]);
                        plNumber = Integer.parseInt(temp[1]);
                        Card card = new Card(plPattern, plNumber);
                        this.grave.add(card);
                    }
                }

            }


        } catch (FileNotFoundException e) {
            System.err.println("파일을 열 수 없습니다.");
        } catch (IOException e) {
            System.err.println("파일 읽기 에러");
        }
        finally {
            if(fReader!=null) {
                try {
                    bReader.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /* 스택 파일에서 읽어오는 메소드 - 민정 */
    /* 민정 - 데이터 파일의 Stack부분 읽어서 stack에 카드 넣는 메소드 */
    private void readAddStack() {
        String line = "";
        int plPattern;
        int plNumber;
        try{
            File file = new File(FILE_NAME);
            fReader = new FileReader(file);
            bReader = new BufferedReader(fReader);

            while((line=bReader.readLine())!=null){

                if(line.contains("Stack")) {
                    while((line=bReader.readLine())!=null) {

                        if(line.contains("/")) {
                            break;
                        }
                        String[] temp = line.split(" ");
                        plPattern = Integer.parseInt(temp[0]);
                        plNumber = Integer.parseInt(temp[1]);
                        Card card = new Card(plPattern, plNumber);
                        this.stack.add(card);
                    }
                }

            }


        } catch (FileNotFoundException e) {
            System.err.println("파일을 열 수 없습니다.");
        } catch (IOException e) {
            System.err.println("파일 읽기 에러");
        }
        finally {
            if(fReader!=null) {
                try {
                    bReader.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }


    // End 판단 메소드
    /* End 입력했는지 판단하는 메소드 */
    private boolean isEnd(Card card) {
        if(card.pattern()==-1 || card.number() == -1)
            return true;
        return false;
    }

    // 차례 판단 메소드
    /* 현재 로그인한 사용자 차례인지 판단하는 메소드 */
    private boolean checkMyTurn() {
        if(this.loginPlNum==this.curTurnPlNum)
            return true;
        return false;
    }

    // 공격 판단
    /* 공격 상황 판단 메소드 */
// 공격 판단 메소드
    /* 이전 차례에서 공격이 있었는지 판단하는 메소드 */
    private boolean isAttacked() { // test public
        if(attackArr.size()==0)
            return false;
        return true;
    }

    // 낼 수 있는 카드
    /* 첫번째로 낼 수 있는 카드인지 */
    // 낼 수 있는 카드인지 판단
    /* 일반 카드 - 낼 수 있는지*/
    private boolean isProperCard(Card card) {
        Player curPlayer=players.get(curTurnPlNum);
        Card prevCard = grave.get(grave.size()-1);
        if(isInitPlCard(curPlayer, card) &&(rule.isOkPattern(prevCard,card)|| rule.isSameNumber(prevCard, card)))
            return true;
        return false;
    }

    /* 추가 카드 - 낼 수 있는지: 조커 아닌 경우 숫자만 같아야. 조커인 경우는 패턴만 맞으면 */
    private boolean isProperAddiCard(Card card) {
        Card prevCard = grave.get(grave.size()-1);
        if(rule.isColorJoker(card) || rule.isBlackJoker(card))
            return false; // 조커는 추가 카드로 못냄
        return isInitPlCard(players.get(curTurnPlNum), card) && rule.isSameNumber(prevCard, card); // 숫자만 같아야 한다.
    }

    /* 사용자 패에 있는 카드인지 - 민정 */
// 패 보유 여부 및 선행카드 일치여부
    /* 민정 - 사용자가 낸 카드가 사용자의 패에 있는 카드인지 확인하는 함수 */
    private boolean isInitPlCard(Player player, Card card){

        int plPattern; // 사용자 보유패 패턴정보
        int plNumber; // 사용자 보유패 숫자정보
        int count = 0;
        boolean isRight = true; // 사용자의 보유패 안에 낸 카드에 대한 정보가 있는지 여부

        for(int i=0; i<player.pCard.size(); i++) {
            plPattern = player.pCard.get(i).pattern();
            plNumber = player.pCard.get(i).number();
            if((card.pattern()==plPattern) && (card.number()==plNumber)) {
                count++;
            }

        }

        //만약 낸 카드가 사용자 보유패에 있으면
        if(count>0) {
            isRight = true;
        }else {
            isRight = false;
        }


        return isRight;
    }


    // 카드 변경 상황
    /* 사용자 패 묘지로 옯기기 - 민정 */
// 사용자가 올바른 카드 낸 이후
    /* 민정 - 사용자 패에서 지우고 묘지에 추가하는 함수 */
    private void plCardToGrave(Player player, Card card) {
        int plPattern; // 사용자 보유패 패턴정보
        int plNumber; // 사용자 보유패 숫자정보

        for(int i=0; i<player.pCard.size(); i++) {
            plPattern = player.pCard.get(i).pattern();
            plNumber = player.pCard.get(i).number();
            if((card.pattern()==plPattern) && (card.number()==plNumber)) {
                Card copyCard = new Card(plPattern, plNumber); // 낸 카드 정보 가져오고
                this.grave.add(copyCard); // grave 에 추가 후
                player.pCard.remove(i); // 사용자 패에서 지움
            }
        }

    }

    /* 공격카드 공격 배열에 옯기기 - 민정 */// 벌칙카드

    /* 민정 - *카드 낸 사용자와 벌칙카드 개수를 배열로 만들어서 attackArr에 넣어주기 **/
    //2 : 2장 / A : 3장 / 스페이드A : 4장 / 흑백조커 : 5장 / 컬러조커 : 7장
    //이미 card는 공격카드임을 확인한 상태라고 가정하고 만든 함수
    public void addAttackArr(int curPlNum, Card card) { // 현재 플레이어 번호, 공격카드를 인자로
        int[] innerArr=new int[4];
        innerArr[0] = curPlNum;

        if(card.number()==2) {
            innerArr[1] = 2;
        }
        if(card.number()==1) {//A일 때
            if(card.pattern()==2) {
                innerArr[1]=4; //스페이드 A : 4장
            }else {
                innerArr[1]=3; //그냥 A : 3장
            }

        }
        if((card.pattern()==4)&&(card.number()==1)) {
            innerArr[1]=5;
        }
        if((card.pattern()==4)&&(card.number()==0)) {
            innerArr[1]=7;
        }

        innerArr[2] = card.pattern();
        innerArr[3] = card.number();

        this.attackArr.add(innerArr);

    }





    // 특수카드 수행 메소드
    /* 특수카드 수행 */
    private void applySpecialRules(Card card) throws InterruptedException {
        if(rule.isJump(card)&&option.get(0)==1) {
            turnKeyJ++;
            return;
        }
        if(rule.isQueen(card)&&option.get(1)==1) {
            turnKeyQ++;
            return;
        }
        if(rule.isKing(card)&&option.get(2)==1) {
            king(card);
        }
        if(rule.isColorJoker(card)&&option.get(3)==1) {
            specialJoker();
        }

    }

    // King
    /* King 냈을 때 수행함수 - 은찬 */
    private void king(Card card) throws InterruptedException{
        
        // King을 내서 선행카드가 king인 화면을 출력 
        clearConsole();
        showCards();
        
        // 한번 더 사용자 입력 받기 (같은 문양 카드 한 장 더 입력 or 카드 한 장 먹기)
        System.out.println("같은 문양의 카드를 한 장 더 낼 수 있습니다.");
        Card nextCard = rule.scanner();

        // 제시한 카드가 같은 문양이면
        if(rule.isOkPattern(card,nextCard)) {
            // 제시한 카드 패에서 선행카드로 바꿔주고
            plCardToGrave(players.get(curTurnPlNum),nextCard);

            //화면 지운 후 종료화면 출력
            clearConsole();
            showTerminalNormal();
        }

        // end 입력한 경우
        else if(isEnd(nextCard)) {
            // 카드 한장 먹기
            eatCard(players.get(curTurnPlNum));

            // 화면 지운 후 종료화면 출력
            clearConsole();
            showTerminalNoCard();
        }

        // 둘 중 하나가 아니면 오류메세지 출력
        else {
            System.out.println("--- 잘못된 입력입니다. 같은 문양의 카드 또는 ‘End’ 명령어를 입력해주세요. ---");
            king(card);
        }
    }

    // special Joker
    /* 은찬 - 특수카드 조커 - 메인함수 *///------------- 수정 필요
    private void specialJoker() throws InterruptedException{
        //컬러조커 특수규칙을 발현할 것인지 선택
        try {
            // 화면 새로 보이기
            clearConsole();
            showCards();

            System.out.println("어떤 기능을 사용하시겠습니까?\n" +
                    "1번 기능: 다음 차례의 사용자에게 7장의 카드를 공격합니다.\n" +
                    "2번 기능: 지정한 사용자와 패를 바꿉니다.");

            int input = sc.nextInt();
            sc.nextLine();

            if (input == 1) {
                addAttackArr(curTurnPlNum, new Card(4, 0));
                plCardToGrave(players.get(curTurnPlNum), new Card(4,0));
                System.out.println("컬러조커가 공격카드로 사용되었습니다.");
                showTerminalNormal();

            } else if (input == 2) {
                selectChangePlayer();

            } else {
                System.out.println("1 또는 2를 입력해주세요.");
                specialJoker();
            }
        } catch (InputMismatchException e){
            System.out.println("1 또는 2를 입력해주세요.");
            specialJoker();
        }
    }

    /* 은찬 - 패 바꿀 사용자 선택 */
    private void selectChangePlayer() throws InterruptedException { // 바꿀 사용자 선택
        try {
            clearConsole();
            showCards();
            System.out.println("패를 바꿀 사용자를 입력해주세요.");
            for (int i = 1; i < totalPlNum + 1; i++) {
                if ((i-1) != curTurnPlNum)
                    System.out.println("  " + i + ". 사용자 " + i + "(" + players.get(i - 1).pCard.size() + "개)");
            }
            System.out.println("  B. 뒤로 가기");

            String str = sc.nextLine();
            str = str.trim();

            boolean isNumber = true;
            // 받은 문자열이 숫자면 true
            for (int i = 0; i < str.length(); i++) {
                char temp = str.charAt(i);
                if (!Character.isDigit(temp))
                    isNumber = false;
            }

            if (isNumber && str.length()!=0) { // 입력이 숫자일 때
                if (players.get(Integer.parseInt(str) - 1).pCard.size() == 0) {
                    System.out.println("선택한 사용자는 게임을 종료하였습니다.");
                    selectChangePlayer();
                } else if ((Integer.parseInt(str) - 1) == curTurnPlNum) {
                    System.out.println("본인이 아닌 다른 사용자를 입력해주세요.");
                    selectChangePlayer();
                } else {
                    System.out.println("--- 사용자 "+curTurnPlNum+"와 사용자 "+(Integer.parseInt(str) - 1)+" 의 카드가 뒤바뀌었습니다. ---\n");
                    // 패 변경
                    specialJokerChange(players.get(curTurnPlNum), players.get(Integer.parseInt(str) - 1));

                    // 종료화면 출력
                    clearConsole();
                    showTerminalNormal();
                }
            } else { // 입력에 문자가 포함되어 있을 때
                if (str.equals("B") || str.equals("b") || str.equals("Back") || str.equals("back")) { // 뒤로가기
                    specialJoker();
                } else {
                    System.out.println("사용자 번호 혹은 뒤로가기 명령어를 입력해주세요.");
                    selectChangePlayer();
                }
            }
        } catch (IndexOutOfBoundsException e){
            System.out.println("선택한 사용자는 게임에 참여하지 않습니다.");
            selectChangePlayer();
        }
    }

    /* 은찬 - 패 변경 */
    private void specialJokerChange(Player player1, Player player2) throws InterruptedException { // player1이 본인, player2가 바꿀 대상으로 두 사용자 패 바꿈
        plCardToGrave(players.get(curTurnPlNum), new Card(4,0));
        ArrayList<Card> temp = player1.pCard;
        player1.pCard = player2.pCard;
        player2.pCard = temp;
    }


    // 스택에서 카드 가져오기
    /* 민정 - 사용자 카드먹는 함수 (스택 -> 사용자패) */
    private void eatCard(Player player) {
        // 카드 먹기 전 스택에 카드 있는지
        if(hasCardsInStack()==false)
            moveGraveToStack(); // 없으면 grave에서 가져오기

        int plPattern = this.stack.get(0).pattern();
        int plNumber = this.stack.get(0).number();

        Card card = new Card(plPattern, plNumber);
        player.pCard.add(card);
        this.stack.remove(0);
    }

    /* 민정 - 묘지에 있는 카드 스택에 모두 옮기는 함수 */
    private void moveGraveToStack() {
        int plPattern;
        int plNumber;
        for(int i=0; i<this.grave.size(); i++) {
            plPattern = grave.get(i).pattern();
            plNumber = grave.get(i).number();
            this.grave.remove(i);
            Card card = new Card(plPattern, plNumber);
            this.stack.add(card);
        }
    }

    /* 스택에 카드 있는지 */
    private boolean hasCardsInStack() {
        if(stack.size() == 0)
            return false;
        return true;
    }



    // ask YES / NO
    /* 더 낼 카드 있는지 묻는 메소드 */
    private boolean askMoreCard() {
        System.out.print("\n>_");
        String answer= sc.nextLine();
        return isYes(answer.trim());
    }

    /* 방어할건지 묻는 메소드 */
    private int askDefense() {
        System.out.println("\n>> 방어를 하시겠습니까? (Y/N)");
        String answer = sc.nextLine().trim();
        return isYesNoOthers(answer);
    }


    // y/n
    /* 응답이 yes 인지만 판단  */
    private boolean isYes(String a) {
        a = a.trim();
        if(a.equals("Yes")||a.equals("yes")||a.equals("Y")||a.equals("y")||a.equals("예")||a.equals("ㅇ"))
            return true;
        return false;
    }

    /* 응답이 yes/no/다른 거 인지 판단 */
    private int isYesNoOthers(String a) {
        a = a.trim();
        if(a.equals("Yes")||a.equals("yes")||a.equals("Y")||a.equals("y")||a.equals("예")||a.equals("ㅇ"))
            return 1;
        if(a.equals("No")||a.equals("no")||a.equals("N")||a.equals("n")||a.equals("아니오")||a.equals("ㄴ"))
            return 0;
        return -1;
    }

 // 종료화면 출력 메소드
    /* 일반 종료화면 - 정윤*/
    private void showTerminalNormal() {
       // 순서 갱신 메소드
       resetTurn();
       
       String curEmail=players.get(pastTurnPlNum).getEmail();
        String nextEmail=players.get(nextTurn).getEmail();
        String printStr="\n\n\t\t사용자 "+(this.pastTurnPlNum+1)+" ("+ curEmail +")"+"의 턴이 종료되었습니다.\n"
                + "\t다음 턴은 사용자 "+(this.nextTurn+1)+" ("+ nextEmail +")"+"가 카드를 제시하게 됩니다.\n"
                + "\t\t    게임 실행을 종료하려면 아무 키를 입력해주세요.\n\n";
        System.out.println(printStr);

        // 파일 갱신메소드
        renewFileWriting();
        //  아무키나 누르면 종료되도록
        String answer = sc.nextLine(); //=----------- 수정될수도 
        System.exit(0);
    }

    /* 사용자가 패 1장도 못냈을 때 종료화면 - 정윤*/
    private void showTerminalNoCard() {
       // 순서 갱신 메소드
       resetTurn();
    
       String curEmail=players.get(pastTurnPlNum).getEmail();
        String nextEmail=players.get(nextTurn).getEmail();
        String printStr="\n\n\t   아무 카드도 제시하지 못하였으므로 사용자의 패에 1장의 카드가 추가됩니다.\n"
                + "\t\t사용자 "+(this.pastTurnPlNum+1)+" ("+ curEmail +")"+"의 턴이 종료되었습니다.\n"
                + "\t다음 턴은 사용자 "+(this.nextTurn+1)+" ("+ nextEmail +")"+"가 카드를 제시하게 됩니다.\n"
                + "\t\t    게임 실행을 종료하려면 아무 키를 입력해주세요.\n\n";
        System.out.println(printStr);

        // 파일 갱신메소드
        renewFileWriting();
        //  아무키나 누르면 종료되도록
        String answer = sc.nextLine(); 
        System.exit(0);
    }

    // 특수 카드 5 관련
    /* 특수 카드 5 발현 상황인지 */
    private boolean canSpecial5() {
        // 선행 카드
        Card prevCard=grave.get(grave.size()-1);
        if(hasCardToPlay(prevCard, curTurnPlNum)==false && hasCardFive(curTurnPlNum)) // 낼카드 없고 5있는 경우
            return true;
        return false;
    }

    /* 은찬 - 스페셜 5카드 메인 메소드 */
    public void special5() throws InterruptedException{
        System.out.println("숫자 카드 5를 특수 카드로 낼 수 있습니다.");
        Card c = rule.scanner();

        //사용자에게 있는 카드인 경우
        if(isInitPlCard(players.get(curTurnPlNum),c)&& c.number()==5){ // 제출 후 수정
            plCardToGrave(players.get(curTurnPlNum), c);

            // 화면 지운 후 종료화면 출력
            clearConsole();
            showTerminalNormal();
        }
        // end 입력한 경우
        else if(isEnd(c)) {
            // 카드 한장 먹기
            eatCard(players.get(curTurnPlNum));

            // 화면 지운 후 종료화면 출력
            clearConsole();
            showTerminalNoCard();
        }
        else {
            System.out.println("--- 잘못된 입력입니다. 패에 있는 ‘숫자 카드 5’ 또는 ‘End’ 명령어를 입력해주세요. ---");
            special5();
        }
    }

    /* 선행카드와 비교하여 패 중에 낼 수 있는 카드가 있는지 판단 - 정윤 */   /// ---- 수정 조커도 낼수 있는 카드에 해당  이렇게 했는지 확인..!
    private boolean hasCardToPlay(Card prevCard, int plNum) {
        Player curPlayer=players.get(plNum);
        ArrayList<Card> arr=curPlayer.getPCard();
        for(Card card:arr) {
            if(rule.isOkPattern(prevCard, card))
                return true;
            if(rule.isSameNumber(prevCard, card))
                return true;
        }
        return false;
    }

    /* 사용자가 5카드 가지고 있는지 판단 - 정윤 */
    private boolean hasCardFive(int plNum) {
        Player curPlayer=players.get(plNum);
        ArrayList<Card> arr=curPlayer.getPCard();

        for(Card card:arr) {
            if(card.number()==5)
                return true;
        }
        return false;

    }



    // 사용자 카드 수 0장 여부
    /* n번 사용자의 카드 수 0장인지 판단하는 메소드 */
    private boolean isCardZero(int plNum) {
        return players.get(plNum).getPCard().size() == 0;
    }

    // 파산
    /* 파산 종료화면 -은찬 */
    public void bankruptUI(){

        // 사용자의 모든 패를 묘지로 보냄
    	int firstSize=players.get(curTurnPlNum).pCard.size(); // --- 제출 후 수정
        for (int i = 0; i <firstSize; i++) { // 초기 카드 값을 변수로 받아온후 돌리기 
            plCardToGrave(players.get(curTurnPlNum), players.get(curTurnPlNum).pCard.get(0)); // 완전히 지우기 - 제출 후 수정 
        }

        // UI출력
        System.out.println("사용자 "+(curTurnPlNum+1)+"( "+players.get(curTurnPlNum).email+" )는 패를 "+bankruptCNum+"장 이상 보유하여 파산하였습니다.\n" +
                "      본인의 턴이 더 이상 돌아오지 않으며, 카드를 제시하실 수 없습니다.\n" +
                "           다만, 로그인 시 게임진행 상황을 확인하실 수 있습니다.\n" +
                "------------------------------------------------------------------\n" +
                "                   최종 게임 순위: "+rank+"위");
        rank--; // 다음 사람 등수 업
        // 순서 변경 메소드 ------------- 추가해야함 
        resetTurn(); // 제출 후 수정 
        renewFileWriting();
        System.exit(0); // 프로그램 종료
    }

    /* 파산 카드 설정 메소드 */
    private int setBankruptCNum(int totalPlNum) { return 53/totalPlNum; }

    /* 파산인지 판단하는 메소드 */
    private boolean isBankrupt() {
        // 사용자 패 개수가 파산 카드 수보다 크거나 같은지
        return players.get(curTurnPlNum).getPCard().size()>=this.bankruptCNum;
    }


    // 기본 메소드
    /* 화면 지우는 메소드 */
    private void clearConsole() throws InterruptedException {
		Thread.sleep(500);	
    }

    /* 턴 종료 시 파일에 들어가야할 정보 모두 갱신해서 출력하는 메소드 - 은찬 */
    public void renewFileWriting() {
        String dummy="";
        String line = "";
        try {
            File file = new File("GameData.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferReader = new BufferedReader(fileReader);

            // 삭제할 line전까지 읽어서 가져오기
            // 데이터 파일의 첫 세줄(고정) + 사용자 인원수 만큼의 줄 까지의 정보 dummy에 저장
            for(int i=0; i<3+totalPlNum; i++) {
                line=bufferReader.readLine();
                dummy += line+"\n";
            }
            bufferReader.close();


            // 파일의 첫 줄부터 dummy 먼저 출력
            FileWriter fWriter = new FileWriter(file);
            fWriter.write(dummy);

            // 사용자 카드 출력
            for (int i = 0; i < totalPlNum; i++) { //CardOfPlayer# 부분
                fWriter.write("\nCardOfPlayer" + i + "\n");

                // 해당 사용자의 패를 "pattern number" 로 한줄씩 저장
                for (int j = 0; j < players.get(i).pCard.size(); j++) {
                    Card cards = players.get(i).pCard.get(j);
                    fWriter.write(cards.pattern() + " " + cards.number() + "\n");
                }
                fWriter.write("/"); // 한 플레이어마다 '/'로 마무리
            }

            //  스택배열 출력
            fWriter.write("\nStack\n");
            for (int i = 0; i < stack.size(); i++) {
                fWriter.write(stack.get(i).pattern()+" "+stack.get(i).number()+"\n");
            }
            fWriter.write("/");

            // 묘지 출력
            fWriter.write("\nGrave\n");
            for (int i = 0; i < grave.size(); i++) {
                fWriter.write(grave.get(i).pattern()+" "+grave.get(i).number()+"\n");
            }
            fWriter.write("/");

            // 공격배열 출력
            fWriter.write("\nAttack\n");
            for (int i = 0; i < attackArr.size(); i++) {
                fWriter.write(attackArr.get(i)[0] + " " + attackArr.get(i)[1]+" "+attackArr.get(i)[2]+" "+attackArr.get(i)[3]+"\n");
            }
            fWriter.write("/");

            // Turn 정보 출력
            fWriter.write("\nTurn\n");
            fWriter.write(curTurnPlNum+" "+nextTurn+" "+turnKeyJ+" "+turnKeyQ+"\n");
            fWriter.write("/");

            // 출력 끝!
            fWriter.flush();
            fWriter.close();
        } catch (IOException e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

 // turn 설정 
    /* turn 설정 메소드 - 정윤*/
     private void resetTurn() {
        // 임시로 저장되는 차례 
        
        // Q가 짝수면 - 정상적인 방향으로 게임 진행 
        if(turnKeyQ%2==0) {
           int cnt = curTurnPlNum; 
           int turn = turnKeyJ+1; // 제출후 수정 

           while(turn!=0) {
              if(players.get((totalPlNum +cnt) % totalPlNum).getPCard().size() !=0)
                 turn--;
              cnt++;
           }
           pastTurnPlNum = curTurnPlNum ;
           nextTurn = (totalPlNum +cnt) % totalPlNum;
           System.out.println(pastTurnPlNum);
           System.out.println(nextTurn);
           turnKeyJ=0;
        }
        
        // Q가 홀수면 - 역방향으로 게임 진행
        else {
           int cnt = curTurnPlNum;
           int turn = turnKeyJ+1;
          while(turn!=0) {
             int tempTurn;
              if(cnt<0)
                 tempTurn = totalPlNum - (Math.abs(cnt)%totalPlNum);
              else 
                 tempTurn = cnt ;
              
              if(players.get(tempTurn).getPCard().size() !=0)
                 turn--;
              
              cnt--;
           }
           pastTurnPlNum = curTurnPlNum ;
           if(cnt<0)
             nextTurn = totalPlNum - (Math.abs(cnt)%totalPlNum);
          else 
             nextTurn =  cnt ;
           turnKeyJ=0;
        }
        
        
     }



}
