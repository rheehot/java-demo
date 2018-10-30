package concurrent.volatile_test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * volatile 키워드를 생소하게 생각하는 사람들도 있을 것이다. 이 키워드는 아주 드믈게 사용한다.
 * volatile이 하는 일은 멀티 스레드에서 한 스레드가 공유값을 변경되면, 다른 스레드들도 갱신된 값을 바로 볼 수 있게 해준다.
 * 대부분 의하하게 생각할 것이다. 공유 값이 변경되면 당연히 다른 곳들도 변경되는 거 아닌가?
 * 아니다.
 * 멀티스레드에서 값은 사실 최적화를 위해 변수를 캐시하게 된다. 이 캐시 방법은 환경에 따라 다르다고 한다.
 * 메모리에 올리던 레지스터를 사용하던가 해서 최적화를 한다고 한다. 변수를 사용할 때마다 모두 해당 주소를 계속 보지 않도록 하는 것 같다.
 * 하지만 volatile키워드를 붙이면 해당 상태를 조회할 때마다 직접 변수를 읽어온다는 것이다.
 *
 * 오!! 아주 좋은 기능이다. 그렇다면 이제 동기화 같은 건 필요 없고, volatile만 붙이면 저절로 병렬프로그래밍이 완성되는 것 아닌가?
 * 자바가 대단하군!
 *
 * 방심하긴 이르다.
 * 변수의 내용을 직접 가져온다 했지, 동기화 되었다곤 하지 않았다.
 *
 * 아래 코드를 보자.
 */
public class VolatileTest1 {
    // 분명 우리는 volatile 키워드를 붙였다.
    private volatile static int COUNTER = 0;

    // 각 스레드는 COUNTER에 +1을 할 것이다.
    // 우리 목표는 COUNTER 의 값이 0 1 2 3 4 5 6 7 8 9가 될 것을 예상할 것이다.
    public static void main(String[] args) {
        Runnable r = () -> {
            System.out.print(COUNTER + " ");
            COUNTER++;
        };

        ExecutorService es = Executors.newCachedThreadPool();

        // 10개의 스레드가 한번에 돌기 시작한다.
        for (int i = 0; i < 10; i++) {
            es.execute(r);
        }
        // ExecutorService가 일을 다 끝내고 죽도록 하자.
        es.shutdown();

        /**
         * 실행을 한 번만 하지말고 여러 번 해보길 바란다.
         * 중요한 것은 0 1 2 3 4 5 6 7 8 9가 나오는 것이 아니다.
         * 예측된 값이 안나온 다는 것도 아니다.(물론 이것도 중요하다.)
         * 우리 예측을 벗어난 (틀린) 값이 일관적으로 나온다면 우리는 우리의 로직에 큰 문제가 있다고 생각할 것이다.
         *
         * 하지만 정말 중요한 것은: 값이 계속 변한다는 것이다.
         * 이것은 예측을 할 수 없다.
         * 스레드는 시간을 추상화 하는 것이다. 대부분 우리는 해야하는 일을 다른 곳으로 분리하여 로직을 추상화 한다.
         * 하지만 병렬 프로그래밍은 대부분의 일이 겹쳐서 일어난다. 시간은 희미해지고 그 사이에서 일어나는 일은
         * 명확하지 않다.
         *
         * 여기서 우리가 알아야 할 점은 우리는 그 무질서한 시간의 겹침 안에서 최소한의 질서를 잘못 가정한 것이다.
         * 우리는 volatile이 변경되면 다른 스레드들의 값 또한 바뀔 것이라고 예측했다.
         *
         * 하지만 우리 예상은 달랐다.
         * 왜냐하면
         * 제대로 된 동기가 되어 있지 않았기 때문에다.
         * 예를 들어보자. 위 10개의 스레드가 충분히 계산속도가 빨라서 거의 동시에 COUNTER=0 을 읽은 후 각각 1을 더하고
         * 자신의 Task를 마친다면? 콘솔에 "0 0 0 0 0 0 0 0 0 0 "가 찍힐 것이다.
         *
         * 하지만 항상 그렇지는 않을 것이다. 이것은 희미한 시간의 겹침 속에 하나의 가능성일 뿐이다.
         * 그렇다면 Volatile이 어디에 쓰이면 좋을까?
         *
         * 인터넷에 BEST PRACTICE들을 찾아보는 것이 좋을 것이다.
         * 하나 정도는 알아보자.
         * 다음에는 volatile을 쓸만한 좋은 예가 될 [volatile를 쓰면 좋은 예]를 한번 보자.
         */
    }
}
