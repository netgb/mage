package mage.cards.m;

import mage.abilities.Ability;
import mage.abilities.common.EntersBattlefieldControlledTriggeredAbility;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.CreateTokenCopyTargetEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.filter.FilterPermanent;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.predicate.Predicates;
import mage.filter.predicate.permanent.TokenPredicate;
import mage.game.Game;
import mage.players.Player;

import java.util.UUID;

/**
 * @author TheElk801
 */
public final class MirrorMarch extends CardImpl {

    private static final FilterPermanent filter = new FilterCreaturePermanent("nontoken creature");

    static {
        filter.add(Predicates.not(new TokenPredicate()));
    }

    public MirrorMarch(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ENCHANTMENT}, "{5}{R}");

        // Whenever a nontoken creature enters the battlefield under your control, flip a coin until you lose a flip. For each flip you won, create a token that's a copy of that creature. Those tokens gain haste. Exile them at the beginning of the next end step.
        this.addAbility(new EntersBattlefieldControlledTriggeredAbility(new MirrorMarchEffect(), filter));
    }

    private MirrorMarch(final MirrorMarch card) {
        super(card);
    }

    @Override
    public MirrorMarch copy() {
        return new MirrorMarch(this);
    }
}

class MirrorMarchEffect extends OneShotEffect {

    MirrorMarchEffect() {
        super(Outcome.Benefit);
        staticText = "flip a coin until you lose a flip. For each flip you won, " +
                "create a token that's a copy of that creature. " +
                "Those tokens gain haste. Exile them at the beginning of the next end step.";
    }

    private MirrorMarchEffect(final MirrorMarchEffect effect) {
        super(effect);
    }

    @Override
    public MirrorMarchEffect copy() {
        return new MirrorMarchEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(source.getControllerId());
        if (player == null) {
            return false;
        }
        int counter = 0;
        boolean wonFlip = false;
        do {
            wonFlip = player.flipCoin(game);
            if (wonFlip) {
                counter++;
            }
        } while (wonFlip);
        if (counter > 0) {
            CreateTokenCopyTargetEffect effect
                    = new CreateTokenCopyTargetEffect(player.getId(), null, true, counter);
            effect.apply(game, source);
            effect.exileTokensCreatedAtNextEndStep(game, source);
        }
        return true;
    }
}