package net.causw.application;

import net.causw.application.dto.CircleFullDto;
import net.causw.application.dto.UserFullDto;
import net.causw.application.spi.CirclePort;
import net.causw.application.spi.UserPort;
import net.causw.domain.exceptions.BadRequestException;
import net.causw.domain.exceptions.ErrorCode;
import net.causw.domain.model.Role;

/**
 * The delegation process for the leader of the circle.
 * The leader of the circle entity is updated.
 * The user who is leader become COMMON state in this process.
 */
public class DelegationLeaderCircle implements Delegation {

    private final UserPort userPort;

    private final CirclePort circlePort;

    public DelegationLeaderCircle(UserPort userPort, CirclePort circlePort) {
        this.userPort = userPort;
        this.circlePort = circlePort;
    }

    @Override
    public void delegate(String currentId, String targetId) {
        CircleFullDto circleFullDto = this.circlePort.findByLeaderId(currentId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "Invalid leader id"
                )
        );

        UserFullDto newLeader = this.userPort.findById(targetId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "Invalid user id"
                )
        );

        this.circlePort.updateLeader(circleFullDto.getId(), newLeader).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "Invalid circle id"
                )
        );

        this.userPort.updateRole(currentId, Role.COMMON).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "Invalid login user id"
                )
        );
    }
}
