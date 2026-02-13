package dev.pig.stockpig.chess.core.bitboard;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.LongConsumer;

/**
 * Magics provides attack map lookups by occupancy using magic bitboards.
 */
public final class Magics {

    /**
     * Get a rook attack map for a square given an occupancy bitboard.
     * @param sq square
     * @param occupied occupied bitboard
     * @return rook attack map
     */
    public static long rAttack(final byte sq, final long occupied) {
        final long occ = occupied & ROOK_MASKS[sq];
        final int idx = (int) ((occ * ROOK_MAGICS[sq]) >>> (64 - ROOK_BITS));
        return ROOK_ATTACKS[sq][idx];
    }

    /**
     * Get a bishop attack map for a square given an occupancy bitboard.
     * @param sq square
     * @param occupied occupied bitboard
     * @return bishop attack map
     */
    public static long bAttack(final byte sq, final long occupied) {
        final long occ = occupied & BISHOP_MASKS[sq];
        final int idx = (int) ((occ * BISHOP_MAGICS[sq]) >>> (64 - BISHOP_BITS));
        return BISHOP_ATTACKS[sq][idx];
    }


    // ====================================================================================================
    //                                  Pre-computed Lookups
    // ====================================================================================================

    private static final int ROOK_BITS = 13;
    private static final long[] ROOK_MASKS = new long[64];
    private static final long[] ROOK_MAGICS = new long[64];
    private static final long[][] ROOK_ATTACKS = new long[64][];
    static {
        // Rook magics
        ROOK_MAGICS[0] = -1764641714668130773L;
        ROOK_MAGICS[1] = 1553931874201819350L;
        ROOK_MAGICS[2] = -1976393152156838640L;
        ROOK_MAGICS[3] = -8166264249320528228L;
        ROOK_MAGICS[4] = -6810301390524964483L;
        ROOK_MAGICS[5] = 7274726727152730578L;
        ROOK_MAGICS[6] = 6342219357786581551L;
        ROOK_MAGICS[7] = -7829956488960440705L;
        ROOK_MAGICS[8] = 2085076643188970896L;
        ROOK_MAGICS[9] = -5323305223493333597L;
        ROOK_MAGICS[10] = -3435199031308881904L;
        ROOK_MAGICS[11] = 6346834789647082191L;
        ROOK_MAGICS[12] = -8535662834000199771L;
        ROOK_MAGICS[13] = -8652945367330313598L;
        ROOK_MAGICS[14] = 6619903103420196588L;
        ROOK_MAGICS[15] = -7125654080792116864L;
        ROOK_MAGICS[16] = -9012959304661143246L;
        ROOK_MAGICS[17] = -5870574759572753632L;
        ROOK_MAGICS[18] = -1351491047909795121L;
        ROOK_MAGICS[19] = -698288156309062621L;
        ROOK_MAGICS[20] = 4050590139777709536L;
        ROOK_MAGICS[21] = -7026537847590144014L;
        ROOK_MAGICS[22] = 180980433445731868L;
        ROOK_MAGICS[23] = -3855278064567087124L;
        ROOK_MAGICS[24] = 7207267283434941113L;
        ROOK_MAGICS[25] = 8569374871132356799L;
        ROOK_MAGICS[26] = -6418242885507577012L;
        ROOK_MAGICS[27] = -3436708606730808862L;
        ROOK_MAGICS[28] = -2790049884078701646L;
        ROOK_MAGICS[29] = -7698101523983196620L;
        ROOK_MAGICS[30] = 8459902803267027179L;
        ROOK_MAGICS[31] = 343426525009012249L;
        ROOK_MAGICS[32] = -3303039165163162648L;
        ROOK_MAGICS[33] = -3829724781468201045L;
        ROOK_MAGICS[34] = -690007553108673158L;
        ROOK_MAGICS[35] = 928870246192121574L;
        ROOK_MAGICS[36] = -7882015791413133870L;
        ROOK_MAGICS[37] = 6650612851919819659L;
        ROOK_MAGICS[38] = 1273552982904209736L;
        ROOK_MAGICS[39] = 1603096638498010764L;
        ROOK_MAGICS[40] = -3090135800389223044L;
        ROOK_MAGICS[41] = 7364353627530798243L;
        ROOK_MAGICS[42] = -2234608700107670784L;
        ROOK_MAGICS[43] = 8640929971394967938L;
        ROOK_MAGICS[44] = 7955694989707727903L;
        ROOK_MAGICS[45] = 6283056529531708428L;
        ROOK_MAGICS[46] = 5862822562677209749L;
        ROOK_MAGICS[47] = 3702830513447917765L;
        ROOK_MAGICS[48] = 7119997551907311104L;
        ROOK_MAGICS[49] = -8685792046912554445L;
        ROOK_MAGICS[50] = 6593451355232664986L;
        ROOK_MAGICS[51] = -6185558184821513545L;
        ROOK_MAGICS[52] = 2811960753742166826L;
        ROOK_MAGICS[53] = 8875574726744206824L;
        ROOK_MAGICS[54] = -6831557805357749327L;
        ROOK_MAGICS[55] = -3533192169946859982L;
        ROOK_MAGICS[56] = -3743120148910777458L;
        ROOK_MAGICS[57] = 5396771140088653998L;
        ROOK_MAGICS[58] = -4337374435235762242L;
        ROOK_MAGICS[59] = -7398201202437894602L;
        ROOK_MAGICS[60] = -460812039191606726L;
        ROOK_MAGICS[61] = -4841898985699228266L;
        ROOK_MAGICS[62] = -7728539716595739740L;
        ROOK_MAGICS[63] = 4645483376557517874L;

        // Init rook masks and attacks
        for (byte i = 0; i < ROOK_MASKS.length; i++) {
            ROOK_MASKS[i] = occupancyMask(i, true);
            ROOK_ATTACKS[i] = attacks(ROOK_MAGICS[i], i, ROOK_BITS, true);
        }
    }

    private static final int BISHOP_BITS = 11;
    private static final long[] BISHOP_MASKS = new long[64];
    private static final long[] BISHOP_MAGICS = new long[64];
    private static final long[][] BISHOP_ATTACKS = new long[64][];
    static {
        // Bishop magics
        BISHOP_MAGICS[0] = 2431709977699446042L;
        BISHOP_MAGICS[1] = -3866846835009527458L;
        BISHOP_MAGICS[2] = 7930737948457096424L;
        BISHOP_MAGICS[3] = 96994123158598031L;
        BISHOP_MAGICS[4] = -8469992239493934621L;
        BISHOP_MAGICS[5] = -511995957634615608L;
        BISHOP_MAGICS[6] = -7554052453446974801L;
        BISHOP_MAGICS[7] = 5618070946589332356L;
        BISHOP_MAGICS[8] = -5330768900092182953L;
        BISHOP_MAGICS[9] = 4801366952945802679L;
        BISHOP_MAGICS[10] = 912001340966193615L;
        BISHOP_MAGICS[11] = -4808396292310802071L;
        BISHOP_MAGICS[12] = -3678710507519967634L;
        BISHOP_MAGICS[13] = 6230805089409751237L;
        BISHOP_MAGICS[14] = -4931379546675110411L;
        BISHOP_MAGICS[15] = 7902488548467015235L;
        BISHOP_MAGICS[16] = 6297197668343750090L;
        BISHOP_MAGICS[17] = 5903511864364771568L;
        BISHOP_MAGICS[18] = 3228562994674495880L;
        BISHOP_MAGICS[19] = -7438762308697650245L;
        BISHOP_MAGICS[20] = 1392128346876953766L;
        BISHOP_MAGICS[21] = 2988134272843164372L;
        BISHOP_MAGICS[22] = -1951620785501831208L;
        BISHOP_MAGICS[23] = 1464965023292126218L;
        BISHOP_MAGICS[24] = 3221034049941829941L;
        BISHOP_MAGICS[25] = 4265949067270542143L;
        BISHOP_MAGICS[26] = -4521119865483286181L;
        BISHOP_MAGICS[27] = -6480213033341195649L;
        BISHOP_MAGICS[28] = 2306487889069895996L;
        BISHOP_MAGICS[29] = 7770854995078135876L;
        BISHOP_MAGICS[30] = 2092742483804883292L;
        BISHOP_MAGICS[31] = 7666552898211762104L;
        BISHOP_MAGICS[32] = 2014168254645777360L;
        BISHOP_MAGICS[33] = -3539152796432375430L;
        BISHOP_MAGICS[34] = 7514660740511428614L;
        BISHOP_MAGICS[35] = -1335684624294474472L;
        BISHOP_MAGICS[36] = -7717200480439574671L;
        BISHOP_MAGICS[37] = 6095626364703417615L;
        BISHOP_MAGICS[38] = 4023077316810062648L;
        BISHOP_MAGICS[39] = -5148638683842110848L;
        BISHOP_MAGICS[40] = 4178041591629611395L;
        BISHOP_MAGICS[41] = -1114815893726911644L;
        BISHOP_MAGICS[42] = -2791320079357121675L;
        BISHOP_MAGICS[43] = 258685861588616455L;
        BISHOP_MAGICS[44] = -3989239402895231627L;
        BISHOP_MAGICS[45] = 9151023106526810924L;
        BISHOP_MAGICS[46] = 3550898146888225073L;
        BISHOP_MAGICS[47] = -7414658547492645026L;
        BISHOP_MAGICS[48] = 7612716388639658866L;
        BISHOP_MAGICS[49] = -5525052689000483220L;
        BISHOP_MAGICS[50] = 4724922320494265476L;
        BISHOP_MAGICS[51] = -1176637071003477820L;
        BISHOP_MAGICS[52] = 8385931067570517431L;
        BISHOP_MAGICS[53] = 401258065180861417L;
        BISHOP_MAGICS[54] = 7349184376780408970L;
        BISHOP_MAGICS[55] = 6953488945052359127L;
        BISHOP_MAGICS[56] = 8783532032126856277L;
        BISHOP_MAGICS[57] = 8511236859466509727L;
        BISHOP_MAGICS[58] = -7052066127893244821L;
        BISHOP_MAGICS[59] = 490398278937701138L;
        BISHOP_MAGICS[60] = -2828387628473594169L;
        BISHOP_MAGICS[61] = -8947212395862616086L;
        BISHOP_MAGICS[62] = 8532124387492613972L;
        BISHOP_MAGICS[63] = 3176925572122216991L;

        // Init bishop masks and attacks
        for (byte i = 0; i < BISHOP_MASKS.length; i++) {
            BISHOP_MASKS[i] = occupancyMask(i, false);
            BISHOP_ATTACKS[i] = attacks(BISHOP_MAGICS[i], i, BISHOP_BITS, false);
        }
    }


    // ====================================================================================================
    //                                  Finding Magics
    // ====================================================================================================

    /**
     * Find dense rook and bishop magic numbers and print in Java format.
     * @param args empty
     */
    public static void main(final String[] args) {
        for (byte i = 0; i < 64; i++) {
            System.out.printf("ROOK_MAGICS[%d] = %dL;%n", i, findMagic(i, 12, true));
        }
        System.out.println();
        for (byte i = 0; i < 64; i++) {
            System.out.printf("BISHOP_MAGICS[%d] = %dL;%n", i, findMagic(i, 10, false));
        }
    }

    /**
     * Find a magic number for the given square with a given attack index bit size.
     * @param sq square
     * @param bits attack index bit size
     * @param isRook find rook magic, else bishop magic
     * @return magic number
     */
    public static long findMagic(final byte sq, final int bits, final boolean isRook) {
        while (true) {
            final long magic = ThreadLocalRandom.current().nextLong();
            if (isMagic(magic, sq, bits, isRook)) return magic;
        }
    }

    /**
     * Get whether a given number is a magic with given attack index bit size.
     * @param magic candidate magic number
     * @param sq square
     * @param bits attack index bit size
     * @param isRook is rook magic, else bishop magic
     * @return is magic
     */
    public static boolean isMagic(final long magic, final byte sq, final int bits, final boolean isRook) {
        final long mask = isRook ? ROOK_MASKS[sq] : BISHOP_MASKS[sq];
        final long piece = Bitboard.ofSquare(sq);

        final long[] attacks = new long[1 << bits];
        final boolean[] used = new boolean[1 << bits];

        long occ = 0L;
        do {
            final int idx = (int) ((occ * magic) >>> (64 - bits));
            final long unoccupied = ~(occ | piece);
            final long attack = isRook ? Bitboard.slideOrthogonal(piece, unoccupied) : Bitboard.slideDiagonal(piece, unoccupied);

            if (!used[idx]) {
                attacks[idx] = attack;
                used[idx] = true;
            } else if (attacks[idx] != attack) {
                return false;
            }

            occ = (occ - mask) & mask;
        } while (occ != 0L);

        return true;
    }


    // ====================================================================================================
    //                                  Helpers
    // ====================================================================================================

    /**
     * Call the consumer with every permutation subset of the mask.
     * Used for finding magics.
     * @param occupancy occupancy mask
     * @param c occupancy consumer
     */
    private static void forEachOccupancy(final long occupancy, final LongConsumer c) {
        long occ = 0L;
        do {
            c.accept(occ);
            occ = (occ - occupancy) & occupancy;
        } while (occ != 0L);
    }

    /**
     * Build the occupancy mask for a given square.
     * @param sq square
     * @param isRook is rook occupancy mask, else bishop occupancy mask
     * @return occupancy mask
     */
    private static long occupancyMask(final byte sq, final boolean isRook) {
        final long bb = Bitboard.ofSquare(sq);
        return bb ^ (isRook ?   Bitboard.fillInto(bb, Direction.E,  ~Bitboard.FILE_H) |
                                Bitboard.fillInto(bb, Direction.W,  ~Bitboard.FILE_A) |
                                Bitboard.fillInto(bb, Direction.N,  ~Bitboard.RANK_8) |
                                Bitboard.fillInto(bb, Direction.S,  ~Bitboard.RANK_1)
                            :   Bitboard.fillInto(bb, Direction.NE, ~(Bitboard.FILE_H | Bitboard.RANK_8)) |
                                Bitboard.fillInto(bb, Direction.NW, ~(Bitboard.FILE_A | Bitboard.RANK_8)) |
                                Bitboard.fillInto(bb, Direction.SE, ~(Bitboard.FILE_H | Bitboard.RANK_1)) |
                                Bitboard.fillInto(bb, Direction.SW, ~(Bitboard.FILE_A | Bitboard.RANK_1)));
    }

    /**
     * Build the attack array for a given magic number and square.
     * @param magic magic number
     * @param sq square
     * @param bits attack index bit size
     * @param isRook is rook magic, else bishop magic
     * @return attacks
     */
    private static long[] attacks(final long magic, final byte sq, final int bits, final boolean isRook) {
        final long mask = occupancyMask(sq, isRook);
        final long piece = Bitboard.ofSquare(sq);
        final long[] attacks = new long[1 << bits];

        forEachOccupancy(mask, occ -> {
            final long unocc = ~(occ | piece);
            final int idx = (int) ((occ * magic) >>> (64 - bits));
            attacks[idx] = isRook ? Bitboard.slideOrthogonal(piece, unocc) : Bitboard.slideDiagonal(piece, unocc);
        });

        return attacks;
    }


    private Magics() {}
}
