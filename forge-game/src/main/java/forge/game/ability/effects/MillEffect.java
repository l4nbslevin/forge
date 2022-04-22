package forge.game.ability.effects;

import java.util.Map;

import forge.game.Game;
import forge.game.GameLogEntryType;
import forge.game.ability.AbilityKey;
import forge.game.ability.AbilityUtils;
import forge.game.ability.SpellAbilityEffect;
import forge.game.card.Card;
import forge.game.card.CardCollectionView;
import forge.game.card.CardZoneTable;
import forge.game.player.Player;
import forge.game.spellability.SpellAbility;
import forge.game.zone.ZoneType;
import forge.util.Lang;
import forge.util.Localizer;
import forge.util.TextUtil;

public class MillEffect extends SpellAbilityEffect {
    @Override
    public void resolve(SpellAbility sa) {
        final Card source = sa.getHostCard();
        final Game game = source.getGame();
        final int numCards = sa.hasParam("NumCards") ? AbilityUtils.calculateAmount(sa.getHostCard(), sa.getParam("NumCards"), sa) : 1;
        final boolean bottom = sa.hasParam("FromBottom");
        final boolean facedown = sa.hasParam("ExileFaceDown");
        final boolean reveal = !sa.hasParam("NoReveal");
        final boolean showRevealDialog = sa.hasParam("ShowMilledCards");

        if (sa.hasParam("ForgetOtherRemembered")) {
            source.clearRemembered();
        }

        ZoneType destination = ZoneType.smartValueOf(sa.getParam("Destination"));
        if (destination == null) {
            destination = ZoneType.Graveyard;
        }

        final CardZoneTable table = new CardZoneTable();
        Map<AbilityKey, Object> moveParams = AbilityKey.newMap();
        moveParams.put(AbilityKey.LastStateBattlefield, sa.getLastStateBattlefield());
        moveParams.put(AbilityKey.LastStateGraveyard, sa.getLastStateGraveyard());

        for (final Player p : getTargetPlayers(sa)) {
            if (!p.isInGame()) {
                continue;
            }

            if (sa.hasParam("Optional")) {
                final String prompt = TextUtil.concatWithSpace(Localizer.getInstance().getMessage("lblDoYouWantPutLibraryCardsTo", destination.getTranslatedName()));
                // CR 701.13b
                if (numCards > p.getZone(ZoneType.Library).size() || !p.getController().confirmAction(sa, null, prompt)) {
                    continue;
                }
            }
            final CardCollectionView milled = p.mill(numCards, destination, bottom, sa, table, moveParams);
            // Reveal the milled cards, so players don't have to manually inspect the
            // graveyard to figure out which ones were milled.
            if (!facedown && reveal) { // do not reveal when exiling face down
                if (showRevealDialog) {
                    game.getAction().reveal(milled, p, false);
                }
                StringBuilder sb = new StringBuilder();
                sb.append(p).append(" milled ").append(milled).append(" to ").append(destination);
                p.getGame().getGameLog().add(GameLogEntryType.ZONE_CHANGE, sb.toString());
            }
            if (destination.equals(ZoneType.Exile)) {
                Card host = sa.getOriginalHost();
                if (host == null) {
                    host = sa.getHostCard();
                }
                for (final Card c : milled) {
                    c.setExiledWith(host);
                    if (facedown) {
                        c.turnFaceDown(true);
                    }
                }
            }
            if (sa.hasParam("RememberMilled")) {
                source.addRemembered(milled);
            }
            if (sa.hasParam("Imprint")) {
                source.addImprintedCards(milled);
            }
        }

        // run trigger if something got milled
        table.triggerChangesZoneAll(game, sa);
    }

    @Override
    protected String getStackDescription(SpellAbility sa) {
        final StringBuilder sb = new StringBuilder();
        final int numCards = sa.hasParam("NumCards") ? AbilityUtils.calculateAmount(sa.getHostCard(), sa.getParam("NumCards"), sa) : 1;

        sb.append(Lang.joinHomogenous(getTargetPlayers(sa))).append(" ");

        final ZoneType dest = ZoneType.smartValueOf(sa.getParam("Destination"));
        if ((dest == null) || dest.equals(ZoneType.Graveyard)) {
            sb.append("mills ");
        } else if (dest.equals(ZoneType.Exile)) {
            sb.append("exiles ");
        } else if (dest.equals(ZoneType.Ante)) {
            sb.append("antes ");
        }

        sb.append(Lang.nounWithNumeralExceptOne(numCards, "card")).append(".");

        return sb.toString();
    }
}
