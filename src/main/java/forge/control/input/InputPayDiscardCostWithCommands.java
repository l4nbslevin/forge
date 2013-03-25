/*
 * Forge: Play Magic: the Gathering.
 * Copyright (C) 2011  Forge Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package forge.control.input;

import java.util.List;
import forge.Card;
import forge.CardLists;
import forge.Singletons;
import forge.card.cardfactory.CardFactoryUtil;
import forge.card.cost.CostDiscard;
import forge.card.spellability.SpellAbility;
import forge.game.player.Player;
import forge.game.zone.ZoneType;
import forge.gui.match.CMatchUI;
import forge.view.ButtonUtil;

//if cost is paid, Command.execute() is called

/**
 * <p>
 * Input_PayManaCost_Ability class.
 * </p>
 * 
 * @author Forge
 * @version $Id: InputPayManaCostAbility.java 15673 2012-05-23 14:01:35Z ArsenalNut $
 */
public class InputPayDiscardCostWithCommands extends InputSyncronizedBase implements InputPayment {
    /**
     * Constant <code>serialVersionUID=2685832214529141991L</code>.
     */
    private static final long serialVersionUID = 5634078561074764401L;

    private int numChosen = 0;
    private int numRequired = 0;
    private List<Card> choiceList;
    private CostDiscard discardCost;
    private SpellAbility ability;
    private boolean bPaid;
    public final boolean isPaid() { return bPaid; }

    /**
     * <p>
     * Constructor for Input_PayManaCost_Ability.
     * </p>
     * 
     * @param cost
     *            a {@link forge.card.cost.CostSacrifice} object.
     * @param sa
     *            a {@link forge.card.spellability.SpellAbility} object.
     * @param paidCommand
     *            a {@link forge.Command} object.
     * @param unpaidCommand
     *            a {@link forge.Command} object.
     */
    public InputPayDiscardCostWithCommands(final CostDiscard cost, final SpellAbility sa) {
        final Card source = sa.getSourceCard();
        final Player human = Singletons.getControl().getPlayer();
        this.ability = sa;
        this.discardCost = cost;
        this.choiceList = CardLists.getValidCards(human.getCardsIn(ZoneType.Hand), cost.getType().split(";"), human, source);
        String amountString = cost.getAmount();
        this.numRequired = amountString.matches("[0-9][0-9]?") ? Integer.parseInt(amountString)
                : CardFactoryUtil.xCount(source, source.getSVar(amountString));
    }

    /** {@inheritDoc} */
    @Override
    public void showMessage() {
        final StringBuilder msg = new StringBuilder("Discard ");
        final int nLeft = this.numRequired - this.numChosen;
        msg.append(nLeft).append(" ");
        msg.append(this.discardCost.getDescriptiveType());
        if (nLeft > 1) {
            msg.append("s");
        }
        if (!this.discardCost.getList().isEmpty()) {
            msg.append("\r\nSelected:\r\n");
            for (Card selected : this.discardCost.getList()) {
                msg.append(selected + "\r\n");
            }
        }

        CMatchUI.SINGLETON_INSTANCE.showMessage(msg.toString());
        if (nLeft > 0) {
            ButtonUtil.enableOnlyCancel();
        }
        else {
            ButtonUtil.enableAllFocusOk();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void selectButtonCancel() {
        for (Card selected : this.discardCost.getList()) {
            selected.setUsedToPay(false);
        }
        this.bPaid = false;
        this.stop();
    }

    /** {@inheritDoc} */
    @Override
    public void selectButtonOK() {
        for (Card selected : this.discardCost.getList()) {
            selected.setUsedToPay(false);
            Singletons.getControl().getPlayer().discard(selected, this.ability);
        }
        this.discardCost.addListToHash(this.ability, "Discarded");
        this.bPaid = true;
        this.stop();
    }

    /** {@inheritDoc} */
    @Override
    public void selectCard(final Card card) {
        if (this.discardCost.getList().contains(card)) {
            this.numChosen--;
            card.setUsedToPay(false);
            this.discardCost.getList().remove(card);
            this.choiceList.add(card);
        } 
        else if (this.choiceList.contains(card) && this.numChosen < numRequired) {
            this.numChosen++;
            this.discardCost.addToList(card);
            card.setUsedToPay(true);
            this.choiceList.remove(card);
        }
        this.showMessage();
    }
}
