package com.setcardgameserver.game.model;

import com.setcardgameserver.card.Card;
import com.setcardgameserver.card.Color;
import com.setcardgameserver.card.Quantity;
import com.setcardgameserver.card.Shape;
import lombok.Data;

import java.util.*;

@Data
public class Game {

    private int gameId;
    private UUID player1;
    private UUID player2;
    private GameStatus status;
    private ArrayList<Card> board = new ArrayList<>();
    private UUID winner;
    private ArrayList<Card> cardDeck = new ArrayList<>();
    private final int BOARD_SIZE = 9;
    private UUID blockedBy;
    private ArrayList<Integer> selectedCardIndexes = new ArrayList<>();
    private Map<UUID, Integer> points = new HashMap<>();

    public void createGame(){
        do {
            for (Color color : Color.values()){
                for (Shape shape : Shape.values()){
                    for (Quantity quantity : Quantity.values()){
                        cardDeck.add(new Card(color, shape, quantity));
                    }
                }
            }

            Collections.shuffle(cardDeck);

            for (int i =0; BOARD_SIZE>i; i++){
                board.add(cardDeck.get(0));
                cardDeck.remove(0);
            }
        }while(!hasSet(board));
    }

    public boolean hasSet(ArrayList<Card> cards){
        if (cards.contains(null)){
            ArrayList<Card> tempCards = new ArrayList<>();
            for (Card card : cards){
                if (card != null){
                    tempCards.add(card);
                }
            }
            cards.clear();
            for (Card card : tempCards){
                cards.add(card);
            }
        }
            if(cards.size()>=3){
                ArrayList<Boolean> propertyChecks = new ArrayList<>();
                for (int i=0; 3>i;i++) propertyChecks.add(false);

                for (int i=0; cards.size()>i;i++){
                    for (int j=i+1; cards.size()>j;j++){
                        for (int k=j+1; cards.size()>k;k++){
                            for (int x=0; 3>x;x++) propertyChecks.set(x,false);
                            if (cards.get(i).getColor()==cards.get(j).getColor() && cards.get(i).getColor()==cards.get(k).getColor()) propertyChecks.set(0, true);
                            if (cards.get(i).getColor()!=cards.get(j).getColor() && cards.get(i).getColor() != cards.get(k).getColor() && cards.get(j).getColor()!=cards.get(k).getColor()) propertyChecks.set(0, true);
                            if (cards.get(i).getShape()==cards.get(j).getShape() && cards.get(i).getShape()==cards.get(k).getShape()) propertyChecks.set(1, true);
                            if (cards.get(i).getShape()!=cards.get(j).getShape() && cards.get(i).getShape() != cards.get(k).getShape() && cards.get(j).getShape()!=cards.get(k).getShape()) propertyChecks.set(1, true);
                            if (cards.get(i).getQuantity()==cards.get(j).getQuantity() && cards.get(i).getQuantity()==cards.get(k).getQuantity()) propertyChecks.set(2, true);
                            if (cards.get(i).getQuantity()!=cards.get(j).getQuantity() && cards.get(i).getQuantity() != cards.get(k).getQuantity() && cards.get(j).getQuantity()!=cards.get(k).getQuantity()) propertyChecks.set(2, true);

                            if (!propertyChecks.contains(false)){
                                propertyChecks.clear();
                                return true;
                            }
                        }
                    }
                }
            }

        return false;
    }

    public void addToSelectedCardIndexes(int index){
        if (selectedCardIndexes.size()<3){
            selectedCardIndexes.add(index);
        }
    }

    public void removeFromSelectedCardIndexes(int index){
        if (selectedCardIndexes.contains(index)){
            int i = selectedCardIndexes.indexOf(index);
            selectedCardIndexes.remove(i);
        }
    }

    public int getSelectedCardIndexSize(){
        return selectedCardIndexes.size();
    }

    public void clearSelectedCardIndexes(){
        selectedCardIndexes.clear();
    }

    public ArrayList<Card> getCardsFromIndex(ArrayList<Integer> indexes){
        ArrayList<Card> selectedCards = new ArrayList<>();

        for (int index : indexes) selectedCards.add(board.get(indexes.get(index)));

        return selectedCards;
    }

    public void changeCardsOnBoard(){
            for (int index : selectedCardIndexes){
                if (cardDeck.size()>0) {
                    board.set(index, cardDeck.get(0));
                    cardDeck.remove(0);
                }
                else {
                    board.set(index, null);
                }
            }
            clearSelectedCardIndexes();
    }
}
