/*
 * AhoCorasickDoubleArrayTrie Project
 *      https://github.com/hankcs/AhoCorasickDoubleArrayTrie
 *
 * Copyright 2008-2016 hankcs <me@hankcs.com>
 * You may modify and redistribute as long as this attribution remains.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hankcs.algorithm;

import java.util.*;

/**
 * <p>
 * A state has the following functions
 * </p>
 * <p/>
 * <ul>
 * <li>success; Successfully transferred to another state</li>
 * <li>failure; If you cannot jump along the string, jump to a shallower node</li>
 * <li>emits; Hit a pattern string</li>
 * </ul>
 * <p/>
 * <p>
 * The root node is slightly different, the root node does not failure Function, its “failure” Refers to the transition to the next state according to the string path. Other nodes have failure status.
 * </p>
 *
 * @author Robert Bor
 */
public class State
{

    protected final int depth;

    private State failure = null;

    private Set<Integer> emits = null;
    
    private Map<Character, State> success = new TreeMap<>();

    private int index;

    public State(){
        this(0);
    }

    public State(int depth){
        this.depth = depth;
    }

    public int getDepth(){
        return this.depth;
    }

    public void addEmit(int keyword){
        if (this.emits == null){
            this.emits = new TreeSet<>(Collections.reverseOrder());
        }
        this.emits.add(keyword);
    }

    public Integer getLargestValueId(){
        if (emits == null || emits.isEmpty()) return null;
        return emits.iterator().next();
    }

    public void addEmit(Collection<Integer> emits){
        for (int emit : emits){
            addEmit(emit);
        }
    }

    public Collection<Integer> emit(){
        return this.emits == null ? Collections.<Integer>emptyList() : this.emits;
    }

    public boolean isAcceptable(){
        return this.depth > 0 && this.emits != null;
    }

    public State failure(){
        return this.failure;
    }

    public void setFailure(State failState, int []fail){
        this.failure = failState;
        fail[index] = failState.index;
    }

    /**
     *
     * @param character       
     * @param ignoreRootState 
     * @return
     */
    private State nextState(Character character, boolean ignoreRootState){
        State nextState = this.success.get(character);
        if (!ignoreRootState && nextState == null && this.depth == 0){
            nextState = this;
        }
        return nextState;
    }

    public State nextState(Character character){
        return nextState(character, false);
    }

    public State nextStateIgnoreRootState(Character character){
        return nextState(character, true);
    }

    public State addState(Character character){
        State nextState = nextStateIgnoreRootState(character);
        if (nextState == null){
            nextState = new State(this.depth + 1);
            this.success.put(character, nextState);
        }
        return nextState;
    }

    public Collection<State> getStates(){
        return this.success.values();
    }

    public Collection<Character> getTransitions(){
        return this.success.keySet();
    }

    @Override
    public String toString(){
        final StringBuilder sb = new StringBuilder("State{");
        sb.append("depth=").append(depth);
        sb.append(", ID=").append(index);
        sb.append(", emits=").append(emits);
        sb.append(", success=").append(success.keySet());
        sb.append(", failureID=").append(failure == null ? "-1" : failure.index);
        sb.append(", failure=").append(failure);
        sb.append('}');
        return sb.toString();
    }

    public Map<Character, State> getSuccess(){
        return success;
    }

    public int getIndex(){
        return index;
    }

    public void setIndex(int index){
        this.index = index;
    }
}
