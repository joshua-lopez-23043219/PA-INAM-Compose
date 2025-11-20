package com.example.pa_inam.network




object OlapRepository {



    private const val M_CNT = "[Measures].[Fact Registration Dimfactregistration Count]"

    // ====== MDX ======
    private val MDX_SHIFT = """
        SELECT
            { $M_CNT } ON COLUMNS,
            [Dim Time Dimtime].[Year].MEMBERS
            DIMENSION PROPERTIES MEMBER_CAPTION ON ROWS
        FROM [Data Cubo2]
    """.trimIndent()

    private val MDX_YEAR = """
        SELECT { $M_CNT } ON COLUMNS,
               NON EMPTY
               ORDER(
                  [Dim Time Dimtime].[Year].MEMBERS,
                  VAL([Dim Time Dimtime].[Year].CURRENTMEMBER.MEMBER_CAPTION),
                  BASC
               ) ON ROWS
        FROM [Data Cubo2]
    """.trimIndent()

    private val MDX_PASS_FAIL = """
        WITH
          MEMBER [Measures].[Reprobados] AS
            CoalesceEmpty( ( $M_CNT, [Dim Notes Dimnotes].[Special Note].&[Reprobado] ), 0 )
          MEMBER [Measures].[Total] AS
            CoalesceEmpty( $M_CNT, 0 )
          MEMBER [Measures].[Aprobados] AS
            IIF([Measures].[Total] > [Measures].[Reprobados],
                [Measures].[Total] - [Measures].[Reprobados], 0)
        SELECT { [Measures].[Aprobados], [Measures].[Reprobados] } ON COLUMNS
        FROM [Data Cubo2]
    """.trimIndent()

    private val MDX_TOP_SUBJECTS_REPROB = """
        SELECT { $M_CNT } ON COLUMNS,
               TOPCOUNT(
                 [Dim Subject Dimsubject].[Name Subject].MEMBERS, 10,
                 ( $M_CNT, [Dim Notes Dimnotes].[Special Note].&[Reprobado] )
               ) ON ROWS
        FROM [Data Cubo2]
    """.trimIndent()

    private val MDX_LEVEL_REG = """
        SELECT { $M_CNT } ON COLUMNS,
               NON EMPTY [Dimen Student Dimstudent].[Level Registration].MEMBERS ON ROWS
        FROM [Data Cubo2]
    """.trimIndent()

    // ====== API BASE ======

    private val api = OlapApiClient.service

    private suspend fun runMdxRaw(mdx: String): List<OlapRow> {
        return api.runMdx(MdxRequest(mdx))
    }

    // ====== FUNCIONES TIPADAS QUE USARÁ EL DASHBOARD ======

    // 1) Conteo por turno (ejemplo genérico etiqueta-valor)
    suspend fun getShiftSeries(): List<CategoryValue> {
        val rows = runMdxRaw(MDX_SHIFT)
        return rows.toCategorySeries(
            labelKeySelector = { keys -> keys.first() },           // ej. "Year" o "Shift"
            valueKeySelector = { keys -> keys.last() }             // la medida
        )
    }

    // 2) Conteo por año (ya ordenado)
    suspend fun getYearSeries(): List<CategoryValue> {
        val series = runMdxRaw(MDX_YEAR).toYearCountSeries()
        return series.sortedBy { it.label.filter(Char::isDigit).toIntOrNull() ?: Int.MAX_VALUE }
    }

    // 3) Aprobados vs reprobados
    suspend fun getPassFailStats(): PassFailStats {
        val rows = runMdxRaw(MDX_PASS_FAIL)
        if (rows.isEmpty()) return PassFailStats(0.0, 0.0)

        val obj = rows.first()
        val passedKey = obj.keySet().firstOrNull { it.contains("Aprobados", true) } ?: obj.keySet().first()
        val failedKey = obj.keySet().firstOrNull { it.contains("Reprobados", true) } ?: passedKey

        return PassFailStats(
            passed = obj[passedKey].asSafeDouble(),
            failed = obj[failedKey].asSafeDouble()
        )
    }

    // 4) Top 10 materias con más reprobados
    suspend fun getTopSubjectsReprob(): List<CategoryValue> {
        val rows = runMdxRaw(MDX_TOP_SUBJECTS_REPROB)

        val series = rows.toCategorySeries(
            labelKeySelector = { keys -> keys.first() },  // Name Subject
            valueKeySelector = { keys -> keys.last() }    // Count
        )

        return series
            .groupBy { it.label }
            .map { (label, list) -> CategoryValue(label, list.sumOf { it.value }) }
            .sortedByDescending { it.value }
            .take(10)
    }

    // 5) Nivel de registro
    suspend fun getLevelRegistrationSeries(): List<CategoryValue> {
        val rows = runMdxRaw(MDX_LEVEL_REG)
        return rows.toCategorySeries(
            labelKeySelector = { keys -> keys.first() },   // Level Registration
            valueKeySelector = { keys -> keys.last() }     // Count
        )
    }


// ===================== GÉNERO POR GRUPO =====================

    // Usa el mismo tipo que tus otras series
     fun queryHombresMujeresPorGrupo(codeGroup: String): String = """
        WITH
    MEMBER [Measures].[Hombres] AS
        CoalesceEmpty(
            (
                [Measures].[Fact Registration Dimfactregistration Count],
                [Dim Group Dimgroup].[Code Group].&[$codeGroup],
                [Dimen Student Dimstudent].[Gender Student].&[M]
            ),
            0
        )

    MEMBER [Measures].[Mujeres] AS
        CoalesceEmpty(
            (
                [Measures].[Fact Registration Dimfactregistration Count],
                [Dim Group Dimgroup].[Code Group].&[$codeGroup],
                [Dimen Student Dimstudent].[Gender Student].&[F]
            ),
            0
        )

SELECT 
    { [Measures].[Hombres], [Measures].[Mujeres] } ON COLUMNS
FROM [Data Cubo2];
    """.trimIndent()


    private val service = OlapApiClient.service

    suspend fun getGenderStatsForGroup(codeGroup: String): GenderStats {
        // 1. Armamos la consulta MDX dinámica
        val mdx = queryHombresMujeresPorGrupo(codeGroup)

        // 2. Llamamos al backend usando la MISMA función de siempre
        // 👉 ADAPTA esta línea al método real que ya tienes

        // --- OPCIÓN A: si tu servicio recibe directamente el string MDX ---
        val rows: List<OlapRow> = service.executeMdx(mdx)

        // --- OPCIÓN B: si recibe un JSON con el mdx ---
        // val rows: List<OlapRow> = service.executeMdx(MdxRequest(mdx))

        // 3. Convertimos la lista de filas a tu modelo de dominio
        return rows.toGenderStats()
    }





}