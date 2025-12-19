package rubikscube.patterndatabase;

public class PermutationIndexer {
    private final int N;
    private final int K;
    private final int[] onesCountLookup;
    private final int[] factorials;

    public PermutationIndexer(int N, int K) {
        this.N = N;
        this.K = K;
        this.onesCountLookup = new int[(1 << N) - 1];
        this.factorials = new int[K];

        for (int i = 0; i < (1 << N) - 1; ++i) {
            this.onesCountLookup[i] = Integer.bitCount(i);
        }

        for (int i = 0; i < K; ++i) {
            this.factorials[i] = MathUtils.pick(N - 1 - i, K - 1 - i);
        }
    }

    public int rank(int[] perm) {
        int[] lehmer = new int[K];
        int seen = 0;

        lehmer[0] = perm[0];
        seen |= (1 << (N - 1 - perm[0]));

        for (int i = 1; i < K; ++i) {
            seen |= (1 << (N - 1 - perm[i]));
            int numOnes = this.onesCountLookup[seen >>> (N - perm[i])];
            lehmer[i] = perm[i] - numOnes;
        }

        int index = 0;
        for (int i = 0; i < K; ++i) {
            index += lehmer[i] * this.factorials[i];
        }
        return index;
    }
}
