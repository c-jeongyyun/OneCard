package _oneCard;

import java.util.Scanner;
public class Rule {




	/* 낼 수 있는 문양인지 확인하는 메소드 - 정윤*/
    public boolean isOkPattern(Card prevCard, Card plCard) {
        if(plCard.pattern() <4 && (plCard.pattern()== prevCard.pattern())) // 내는 카드가 조커가 아닌 경우 - 문양이 같으면 됨
            return true;
        if(isColorJoker(plCard) && (prevCard.pattern()==0||prevCard.pattern()==1)) // 내는 카드가 컬러조커인 경우 - 이전카드 문양이 하트 또는 다이아
            return true;
        if(isBlackJoker(plCard) && (prevCard.pattern()==2||prevCard.pattern()==3)) // 내는 카드가 흑백조커인 경우 - 이전카드 문양이 클로버 또는 스페이드
            return true;
        // 선행(이전)카드가 조커일 경우 같은 색의 카드 낼 수 있는 조건 추가 - 은찬 // 제출 후 수정 
        if(isColorJoker(prevCard) && (plCard.pattern()==0||plCard.pattern()==1)) // 선행카드가 컬러조커인 경우 - 내는카드 문양이 하트 또는 다이아
            return true;
        if(isBlackJoker(prevCard) && (plCard.pattern()==2||plCard.pattern()==3)) // 선행카드가 흑백조커인 경우 - 내는카드 문양이 클로버 또는 스페이드
            return true;
        return false;
    }
    
    
    /* 같은 숫자인지 판단하는 메소드 -정윤*/
    public boolean isSameNumber(Card prevCard, Card plCard) {
        if( plCard.pattern()<4 && (plCard.number()== prevCard.number())) // 조커가 아닌 경우 같은 숫자인지 판단
            return true;
        return false;
    }

    /* J인지 판단하는 메소드 - 정윤*/
    public boolean isJump(Card card) {
        if(card.number()==11)
            return true;
        return false;
    }

    /* Q인지 판단하는 메소드 - 정윤*/
    public boolean isQueen(Card card) {
        if(card.number()==12)
            return true;
        return false;
    }

    /* K인지 판단하는 메소드 - 정윤 */
    public boolean isKing(Card card) {
        if(card.pattern()<4 && card.number()==0) // 4,0 은 조커가 됨. 따라서 4 제외 해야한다.
            return true;
        return false;
    }

    /* 컬러 조커인지 판단하는 메소드 - 정윤*/
    public boolean isColorJoker(Card card) {
        if(card.pattern()==4 && card.number()==0)
            return true;
        return false;
    }
    /* 흑백 조커인지 판단하는 메소드 */

    public boolean isBlackJoker(Card card) {
        if(card.pattern()==4 && card.number()==1)
            return true;
        return false;
    }

    /* 민정 - 공격카드인지 아닌지 판단하는 함수 (2 , A , Joker) */
    public boolean isAttackCard(Card card) {
        if((card.number()==2)||(card.number()==1)||(card.pattern()==4)) {
            return true;
        }else {
            return false;
        }

    }

    /* 방어카드 3인지 (선행카드가 2이고, 선행카드와 패턴 같은 3카드인지) - 은찬*/
    public boolean isDefence3(Card prevCard, Card plCard){
        if(prevCard.number()==2 && plCard.pattern()==prevCard.pattern() && plCard.number()==3)
            return true;
        return false;
    }


    /* 높거나 같은 방어카드인지 */
    public boolean isHigherSameAttack(Card prev, Card next) {
        // 같은 수준의 공격카드 이거나 높은 수준의 공격카드이면 true
        if(isSameAttack(prev,next)|| isHigherAttack (prev,next))
            return true;
        return false;
    }


    /* 같은 수준 공격카드인지 */
    private boolean isSameAttack(Card prev, Card next) {
        // 선행 카드와 현재카드 모두 숫자 2인 경우 - 2끼리는 다 같은 수준 -true
        if(prev.number()==2 && next.number()==2)
            return true;

        // 선행 카드와 현재카드 모두 A인 경우
        if(prev.number()==1 && next.number()==1) {
            // 선행카드가 스페이드 A인 경우 - 같은 수준 아님 -false
            if(prev.pattern() == 2)
                return false;

            // 다른 문양 A인 경우 - true
            return true;
        }
        // 위에 해당하지 않는 경우
        return false;

    }

    /* 높은 수준 공격카드인지 */
    private boolean isHigherAttack(Card prev,Card next) {
        int prevPattern = prev.pattern();
        switch(prevPattern) {
            // 선행카드 문양이 컬러인 경우 (하트, 다이아)
            case 0:
            case 1:
                // 선행 카드가 컬러 2일 때,
                if(prev.number()==2) {
                    // 후행 카드 - 컬러 A인 경우
                    if(isRedCard(next) &&next.number()==1)
                        return true;

                    // 후행카드 - 컬러조커인 경우
                    if(isColorJoker(next))
                        return true;
                }

                // 선행카드 문양이 흑색인 경우 (스페이드, 클로버)
            case 2:
            case 3:
                // 선행 카드가 흑색 2일 때,
                if(prev.number()==2) {
                    // 후행 카드 - 흑색 A인 경우
                    if(isBlackCard(next) && next.number()==1)
                        return true;
                    // 후행카드 - 흑백조커인 경우
                    if(isBlackJoker(next))
                        return true;
                }

                // 선행카드가 클로버 A일 때,
                if(prev.pattern() == 3 && prev.number()==1) {
                    // 후행카드 스페이드 A 인 경우
                    if(next.pattern()==2 && next.number()==1)
                        return true;
                    // 후행카드 - 흑백조커인 경우
                    if(isBlackJoker(next))
                        return true;
                }
                // 선행카드가 스페이드 A일 때,
                if(prev.pattern() == 2 && prev.number()==1) {
                    // 후행카드 - 흑백조커인 경우
                    if(isBlackJoker(next))
                        return true;
                }


                // 위 내용 제외한건 다 false
            default: return false;
        }

    }

    private boolean isRedCard(Card card) {
        return card.pattern()==0 || card.pattern()==1;
    }
    private boolean isBlackCard(Card card) {
        return card.pattern()==2 || card.pattern()==3;
    }

  
    /* [사용자의 입력을 모양, 숫자에 맞게 변환시켜주는 메소드] // 제출 후 수정 
    경민+은찬*/
    public Card scanner() {
        while(true) {
        int p=-1,n=-1;
            Scanner scanner = new Scanner(System.in);
            String card_inf;
            String put_pattern="";
            String stringNumber="";
            boolean isSlash = false; //카드 구분자를 구분해내기 위한 변수
            int slashIndex = -1;
            String ans;
                //카드 패턴 및 숫자 카드를 비개행 공백열로 구분해서 입력하기.
                System.out.println(">>카드를 한 장 내주세요");
                card_inf = scanner.nextLine();
                if (card_inf.equals("End") || card_inf.equals("END") || card_inf.equals("end") || card_inf.equals("E") || card_inf.equals("e")) {
                    System.out.println(p + " " + n);
                    return new Card(p, n);
                }
                for (int i = 0; i < card_inf.length(); i++) {
                    if (card_inf.charAt(i) == '/' || card_inf.charAt(i) == '_' || card_inf.charAt(i) == 32 || card_inf.charAt(i) == '\t') {
                        isSlash = true;
                        slashIndex = i;
                    }
                }
                if (isSlash) {
                    put_pattern = String.valueOf(card_inf.charAt(0));
                    stringNumber = String.valueOf(card_inf.charAt(slashIndex + 1));
                    for (int j = slashIndex + 2; j < card_inf.length(); j++) {
                        stringNumber += card_inf.charAt(j);
                    }
                    for (int k = 1; k < slashIndex; k++) {
                        put_pattern += card_inf.charAt(k);
                    }
                    //put_pattern = String.valueOf(put_pattern.charAt(0));
                } else {
                    System.out.println(">>잘못된 입력입니다. 다시 입력해주세요.");
                    continue;
                }

                if (stringNumber.charAt(0) == '@' || put_pattern.charAt(0) == '@') {
                    if (stringNumber.charAt(0) == '@') {
                        if (put_pattern.charAt(0) != 'b' && put_pattern.charAt(0) != 'B' && put_pattern.charAt(0) != 'c' && put_pattern.charAt(0) != 'C') {
                            System.out.println(">>잘못된 입력입니다. 다시 입력해주세요.");
                            continue;
                        } else {
                            if (put_pattern.charAt(0) == 'c' || put_pattern.charAt(0) == 'C') {
                                n = 0;
                                p = 4;
                                return new Card(4, 0);
                            } else {
                                n = 1;
                                p = 4;
                                return new Card(4, 1);
                            }
                        }
                    } else {
                        ans = put_pattern;
                        put_pattern = stringNumber;
                        stringNumber = ans;
                        if (put_pattern.charAt(0) != 'b' && put_pattern.charAt(0) != 'B' && put_pattern.charAt(0) != 'c' && put_pattern.charAt(0) != 'C') {
                            System.out.println(">>잘못된 입력입니다. 다시 입력해주세요.");
                            continue;
                        } else {
                            if (put_pattern.charAt(0) == 'c' || put_pattern.charAt(0) == 'C') {
                                n = 0;
                                p = 4;
                                return new Card(4, 0);
                            } else {
                                n = 1;
                                p = 4;
                                return new Card(4, 1);
                            }
                        }
                    }
                }
                if (stringNumber.charAt(0) != '@' && put_pattern.charAt(0) != '@') {
                    if (stringNumber.charAt(0) == 's' || stringNumber.charAt(0) == 'S' || stringNumber.charAt(0) == 'c' || stringNumber.charAt(0) == 'C' || stringNumber.charAt(0) == 'h' || stringNumber.charAt(0) == 'H' || stringNumber.charAt(0) == 'd' || stringNumber.charAt(0) == 'D') {
                        ans = stringNumber;
                        stringNumber = put_pattern;
                        put_pattern = ans;
                        if (stringNumber.charAt(0) == 'a' || stringNumber.charAt(0) == 'A' || stringNumber.charAt(0) == '2' || stringNumber.charAt(0) == '3' || stringNumber.charAt(0) == '4' || stringNumber.charAt(0) == '5' || stringNumber.charAt(0) == '6' || stringNumber.charAt(0) == '7' || stringNumber.charAt(0) == '8' || stringNumber.charAt(0) == '9' || stringNumber.equals("10") || stringNumber.charAt(0) == 'j' || stringNumber.charAt(0) == 'J' || stringNumber.charAt(0) == 'q' || stringNumber.charAt(0) == 'Q' || stringNumber.charAt(0) == 'k' || stringNumber.charAt(0) == 'K') {
                            switch (put_pattern.charAt(0)) {
                                case 'h':
                                case 'H':
                                    p = 0;
                                    break;
                                case 'd':
                                case 'D':
                                    p = 1;
                                    break;
                                case 's':
                                case 'S':
                                    p = 2;
                                    break;
                                case 'c':
                                case 'C':
                                    p = 3;
                                    break;
                                default:
                                    break;
                            }
                            switch (stringNumber) {
                                case "a":
                                case "A":
                                    n = 1;
                                    break;
                                case "2":
                                    n = 2;
                                    break;
                                case "3":
                                    n = 3;
                                    break;
                                case "4":
                                    n = 4;
                                    break;
                                case "5":
                                    n = 5;
                                    break;
                                case "6":
                                    n = 6;
                                    break;
                                case "7":
                                    n = 7;
                                    break;
                                case "8":
                                    n = 8;
                                    break;
                                case "9":
                                    n = 9;
                                    break;
                                case "10":
                                    n = 10;
                                    break;
                                case "j":
                                case "J":
                                    n = 11;
                                    break;
                                case "q":
                                case "Q":
                                    n = 12;
                                    break;
                                case "k":
                                case "K":
                                    n = 0;
                                    break;
                            }
                            return new Card(p, n);
                        } else {
                            System.out.println(">>잘못된 입력입니다. 다시 입력해주세요.");
                            continue;
                        }
                    } else {
                        if (stringNumber.charAt(0) != 'a' && stringNumber.charAt(0) != 'A' && stringNumber.charAt(0) != '2' && stringNumber.charAt(0) != '3' && stringNumber.charAt(0) != '4' && stringNumber.charAt(0) != '5' && stringNumber.charAt(0) != '6' && stringNumber.charAt(0) != '7' && stringNumber.charAt(0) != '8' && stringNumber.charAt(0) != '9' && !stringNumber.equals("10") && stringNumber.charAt(0) != 'j' && stringNumber.charAt(0) != 'J' && stringNumber.charAt(0) != 'q' && stringNumber.charAt(0) != 'Q' && stringNumber.charAt(0) != 'k' && stringNumber.charAt(0) != 'K') {
                            System.out.println(">>잘못된 입력입니다. 다시 입력해주세요.");
                            continue;
                        } else if (put_pattern.charAt(0) != 's' && put_pattern.charAt(0) != 'S' && put_pattern.charAt(0) != 'c' && put_pattern.charAt(0) != 'C' && put_pattern.charAt(0) != 'h' && put_pattern.charAt(0) != 'H' && put_pattern.charAt(0) != 'd' && put_pattern.charAt(0) != 'D') {
                            System.out.println(">>잘못된 입력입니다. 다시 입력해주세요.");
                            continue;
                        } else {
                            switch (put_pattern.charAt(0)) {
                                case 'h':
                                case 'H':
                                    p = 0;
                                    break;
                                case 'd':
                                case 'D':
                                    p = 1;
                                    break;
                                case 's':
                                case 'S':
                                    p = 2;
                                    break;
                                case 'c':
                                case 'C':
                                    p = 3;
                                    break;
                                default:
                                    break;
                            }
                            switch (stringNumber) {
                                case "a":
                                case "A":
                                    n = 1;
                                    break;
                                case "2":
                                    n = 2;
                                    break;
                                case "3":
                                    n = 3;
                                    break;
                                case "4":
                                    n = 4;
                                    break;
                                case "5":
                                    n = 5;
                                    break;
                                case "6":
                                    n = 6;
                                    break;
                                case "7":
                                    n = 7;
                                    break;
                                case "8":
                                    n = 8;
                                    break;
                                case "9":
                                    n = 9;
                                    break;
                                case "10":
                                    n = 10;
                                    break;
                                case "j":
                                case "J":
                                    n = 11;
                                    break;
                                case "q":
                                case "Q":
                                    n = 12;
                                    break;
                                case "k":
                                case "K":
                                    n = 0;
                                    break;
                            }
                            return new Card(p, n);

                        }
                    }
                }
            }
        }
    }


