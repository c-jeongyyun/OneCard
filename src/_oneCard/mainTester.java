package _oneCard;


public class mainTester {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		// 룰 검사 

		GameReady gameReady = new GameReady();
		
	
		try {
			// 게임 시작
			gameReady.start();
			
			// 옵션 설정 
			gameReady.setOption();
			
			// 사용자 등록
			gameReady.signIn();
			
			// 카드 없을 경우 카드 부여 
			if(gameReady.hasCard()==false) { 
				// 카드 부여 + 파일에 출력 
				gameReady.initFileAfSignIn(); // 카드, 묘지, 차례 초기화 
				
			}
			// 로그인
			gameReady.logIn();
			
			// GameManager 객체 생성
			GameManager gameManager = new GameManager(gameReady.getLoginPlNum(), gameReady.getTotalNum(), gameReady.getOption());
			
			// 한명 빼고 모두 카드 0인 경우 
			if(gameManager.isZeroExceptOne())
				gameManager.resetFlie();
			// 서브 화면 출력 여부 - 내 차례가 아니거나, 카드 수 0인 경우 
			if(gameManager.isSubPrompt()) {
				// 서브화면 함수 -> 여기서 종료 
				gameManager.subMain();
			}
			
			// 본인 차례인 경우 
			gameManager.playCard();
			
			
			

			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
