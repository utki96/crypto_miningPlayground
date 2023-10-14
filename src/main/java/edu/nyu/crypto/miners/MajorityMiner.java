package edu.nyu.crypto.miners;

import edu.nyu.crypto.blockchain.Block;
import edu.nyu.crypto.blockchain.NetworkStatistics;

public class MajorityMiner extends CompliantMiner implements Miner {

    protected Block currentHead;
    protected Block currentMiningBlock;
    protected NetworkStatistics networkStatisticsSnapshot;
    public MajorityMiner(String id, int hashRate, int connectivity) {
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
            if (block.getHeight() > this.currentHead.getHeight()) {
                this.currentHead = block;
            }
            if (block.getHeight() > this.currentMiningBlock.getHeight()) {
                this.currentMiningBlock = block;
            }
        } else {
            if (this.networkStatisticsSnapshot.getTotalHashRate() > 2 * this.getHashRate()) {  // owns less than 50% share
                if (block.getHeight() > this.currentMiningBlock.getHeight()) {
                    this.currentHead = block;
                    this.currentMiningBlock = block;
                } else {
                    this.currentHead = this.currentMiningBlock;
                }
            }
        }
    }

    @Override
    public void initialize(Block genesis, NetworkStatistics networkStatistics) {
        this.currentHead = genesis;
        this.currentMiningBlock = genesis;
        this.networkStatisticsSnapshot = networkStatistics;
    }

    @Override
    public void networkUpdate(NetworkStatistics statistics) {
        this.networkStatisticsSnapshot = statistics;
    }
}
