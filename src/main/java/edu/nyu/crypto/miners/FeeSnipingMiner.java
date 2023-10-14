package edu.nyu.crypto.miners;


import edu.nyu.crypto.blockchain.Block;
import edu.nyu.crypto.blockchain.NetworkStatistics;

public class FeeSnipingMiner extends CompliantMiner implements Miner {

    private Block currentHead;
    private Block currentMiningBlock;
    private double avgReward;
    private int rewardsObserved;

    public FeeSnipingMiner(String id, int hashRate, int connectivity) {
        super(id, hashRate, connectivity);

    }

    @Override
    public Block currentlyMiningAt() {
        return this.currentMiningBlock;
    }

    @Override
    public Block currentHead() {
        return this.currentHead;
    }

    @Override
    public void blockMined(Block block, boolean isMinerMe) {
        if (isMinerMe) {
            if (block.getHeight() > this.currentMiningBlock.getHeight()) {
                this.currentMiningBlock = block;
            }
            if (block.getHeight() >= this.currentHead.getHeight()) {
                this.currentHead = block;
            }
        } else {
            int difference = block.getHeight() - this.currentMiningBlock.getHeight();
            if (difference > 0) {
                this.currentHead = block;
                if (block.getBlockValue() > 1.2 * this.avgReward) {
                    this.currentMiningBlock = block.getPreviousBlock();
                } else {
                    this.currentMiningBlock = block;
                }
            } else {
                this.currentHead = this.currentMiningBlock;
            }
            this.avgReward = (this.rewardsObserved * this.avgReward + block.getBlockValue()) / (this.rewardsObserved + 1);
            this.rewardsObserved++;
        }
    }

    @Override
    public void initialize(Block genesis, NetworkStatistics networkStatistics) {
        this.currentHead = genesis;
        this.currentMiningBlock = genesis;
        this.avgReward = 0.0;
        this.rewardsObserved = 0;
    }

    @Override
    public void networkUpdate(NetworkStatistics statistics) {  }
}
