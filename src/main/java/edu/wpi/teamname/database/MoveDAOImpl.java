package edu.wpi.teamname.database;

import edu.wpi.teamname.navigation.Move;
import lombok.Getter;

import java.util.ArrayList;

public class MoveDAOImpl implements MoveDAO{
    @Getter private ArrayList<Move> moves;

    /**
     *
     */
    @Override
    public void sync() {

    }

    /**
     * @return
     */
    @Override
    public ArrayList<Move> getAll() {
        return null;
    }

    /**
     * @param type
     */
    @Override
    public void add(Move type) {

    }

    /**
     * @param type
     */
    @Override
    public void delete(Move type) {

    }
}