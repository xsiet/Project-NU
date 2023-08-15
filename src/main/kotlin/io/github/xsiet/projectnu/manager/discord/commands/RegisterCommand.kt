package io.github.xsiet.projectnu.manager.discord.commands

import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData

val registerCommand = Commands.slash("등록", "등록 명령어")
    .setGuildOnly(true)
    .addSubcommands(
        SubcommandData("플레이어", "플레이어 데이터를 등록합니다")
            .addOption(OptionType.STRING, "마인크래프트_닉네임", "마인크래프트 닉네임을 입력해 주세요!", true)
            .addOption(
                OptionType.STRING,
                "서버_닉네임",
                "서버에서 사용할 닉네임을 입력해 주세요! (띄어쓰기 사용 불가, 8자 이하)",
                true
            ),
        SubcommandData("팀", "팀 데이터를 등록합니다")
            .addOption(OptionType.STRING, "팀_이름", "팀 이름을 입력해 주세요! (띄어쓰기 포함 8자 이하)", true)
            .addOption(
                OptionType.STRING,
                "팀_능력",
                "팀 능력을 입력해 주세요! (자동완성에 표시되는 것만 입력 가능)",
                true,
                true
            )
            .addOption(OptionType.USER, "팀장", "팀장을 입력해 주세요!", true)
            .addOption(OptionType.USER, "팀원_1", "팀원를 입력해 주세요!")
            .addOption(OptionType.USER, "팀원_2", "팀원를 입력해 주세요!")
            .addOption(OptionType.USER, "팀원_3", "팀원를 입력해 주세요!")
    )
    .addSubcommandGroups(
        SubcommandGroupData("취소", "등록 취소 명령어")
            .addSubcommands(
                SubcommandData("플레이어", "플레이어 데이터 등록을 취소합니다")
            )
    )