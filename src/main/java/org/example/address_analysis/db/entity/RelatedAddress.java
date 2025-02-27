package org.example.address_analysis.db.entity;

import java.util.Objects;

public record RelatedAddress(String address, Long blockNumber, Long blockTime) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RelatedAddress that = (RelatedAddress) o;

        return Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return address != null ? address.hashCode() : 0;
    }
}
