package forge.game.ability.effects;

import forge.game.GameEntity;
import forge.game.ability.AbilityUtils;
import forge.game.ability.SpellAbilityEffect;
import forge.game.card.Card;
import forge.game.card.CardCollection;
import forge.game.player.Player;
import forge.game.player.PlayerCollection;
import forge.game.spellability.SpellAbility;

public class MustAttackEffect extends SpellAbilityEffect {

    /* (non-Javadoc)
     * @see forge.card.abilityfactory.SpellEffect#getStackDescription(java.util.Map, forge.card.spellability.SpellAbility)
     */
    @Override
    protected String getStackDescription(SpellAbility sa) {
        final Card host = sa.getHostCard();
        final StringBuilder sb = new StringBuilder();

        // end standard pre-

        String defender = null;
        if (sa.getParam("Defender").equals("Self")) {
            defender = host.toString();
        } else {
            defender = host.getController().toString();
        }

        for (final Player player : getTargetPlayers(sa)) {
            sb.append("Creatures ").append(player).append(" controls attack ");
            sb.append(defender).append(" during their next turn.");
        }
        for (final Card c : getTargetCards(sa)) {
            sb.append(c).append(" must attack ");
            sb.append(defender).append(" during its controller's next turn if able.");
        }

        return sb.toString();
    }

    @Override
    public void resolve(SpellAbility sa) {
        final String defender = sa.getParam("Defender");
        final boolean thisTurn = sa.hasParam("ThisTurn");
        GameEntity entity = null;
        if (defender.equals("Self")) {
            entity = sa.getHostCard();
        } else {
            PlayerCollection defPlayers = AbilityUtils.getDefinedPlayers(sa.getHostCard(), defender, sa);
            CardCollection defPWs = AbilityUtils.getDefinedCards(sa.getHostCard(), defender, sa);
            if ((defPlayers.isEmpty() && defPWs.isEmpty()) || defPlayers.size() > 1 || defPWs.size() > 1) {
                throw new RuntimeException("Illegal (nonexistent or not uniquely defined) defender " + defender + " for MustAttackEffect in card " + sa.getHostCard());
            }
            if (!defPlayers.isEmpty()) {
                entity = defPlayers.getFirst();
            } else if (!defPWs.isEmpty()) {
                entity = defPWs.getFirst();
            }
        }

        // TODO these should not override but add another requirement
        for (final Player p : getTargetPlayers(sa)) {
            if (!p.isInGame()) {
                continue;
            }
            if (thisTurn || !p.getGame().getPhaseHandler().isPlayerTurn(p)) {
                p.setMustAttackEntityThisTurn(entity);
            } else {
                p.setMustAttackEntity(entity);
            }
        }
        for (final Card c : getTargetCards(sa)) {
            if (thisTurn) {
                c.setMustAttackEntityThisTurn(entity);
            } else {
                c.setMustAttackEntity(entity);
            }
        }
    } // mustAttackResolve()

}
