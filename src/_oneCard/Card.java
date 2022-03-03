package _oneCard;

public class Card {

	 // 필드 (멤버 변수)
    private int pattern; //문양(0:하트, 1:다이아, 2:스페이드, 3:클로버, 4:조커)
    private int number; //숫자&영어카드(0: K, 1: A, 2~10:숫자, 11:J, 12:Q,) 주의 : pattern이 4인 경우, number가 0이면 컬러조커, 1이면 흑백조커
    

    // 생성자 (-1 은 어디에도 해당되지 않는 null값으로 초기화 시킨 것, 세 요소 중 하나라도 -1이 있으면 오류)
    Card() {
        pattern = -1;
        number = -1;
      
    }
    Card(int p, int n) {
        pattern = p;
        number = n;
     
    }
    Card(int p, int n, int o) {
        pattern = p;
        number = n;
      
    }

    // 필드값 호출
    public int pattern() { return pattern; }
    public int number() { return number; }
   

    // 필드값 설정
    public void setPattern(int p) { pattern = p; }
    public void setNumber(int n) { number = n; }
 
}
