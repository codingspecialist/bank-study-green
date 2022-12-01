interface CallAble {
    void 까페라때만들기();

    void 아메리카노만들기();

    void 설탕커피만들기();

}

class 자판기 implements CallAble {
    private void 종이컵준비하기() {
    }

    private void 물담기() {
    }

    private void 커피가루담기() {
    }

    private void 설탕담기() {
    }

    private void 우유담기() {
    }

    @Override
    public void 까페라때만들기() {
        종이컵준비하기();
        물담기();
        커피가루담기();
        우유담기();
    }
}

public class Hello {

    public static void main(String[] args) {
        자판기 s = new 자판기();
        s.까페라때만들기();
    }
}
